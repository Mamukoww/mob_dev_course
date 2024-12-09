package com.example.mob_dev_course.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.core.view.children
import com.example.mob_dev_course.MainActivity
import com.example.mob_dev_course.R
import com.example.mob_dev_course.data.Medication
import com.example.mob_dev_course.data.MedicationStorage
import com.example.mob_dev_course.data.ScheduleData
import com.example.mob_dev_course.data.ScheduleStorage
import com.example.mob_dev_course.data.TimeSchedule
import com.example.mob_dev_course.models.Drug
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class DrugSettingsFragment : Fragment() {
    private lateinit var nameInput: AutoCompleteTextView
    private lateinit var commentInput: EditText
    private lateinit var typeSpinner: Spinner
    private lateinit var frequencySpinner: Spinner
    private lateinit var timesPerDaySpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var timeButtonsContainer: LinearLayout
    private lateinit var addToScheduleButton: Button
    private lateinit var daysSelectionContainer: LinearLayout
    private lateinit var dateRangeContainer: LinearLayout
    private lateinit var startDateButton: Button
    private lateinit var endDateButton: Button
    
    private var startDate: Calendar? = null
    private var endDate: Calendar? = null
    private val timeSchedules = mutableListOf<TimeSchedule>()
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val drugsList = mutableListOf<Drug>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.drug_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация views
        initializeViews(view)

        // Инициализация ScheduleStorage
        context?.let { ScheduleStorage.initialize(it) }

        // Настройка автозаполнения
        setupAutoComplete()
        
        setupSpinners()
        setupDateButtons()
        setupFrequencySpinner()
        setupTimesPerDaySpinner()
        setupSaveButton()
    }

    private fun setupAutoComplete() {
        // Инициализация адаптера
        adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf<String>()
        )
        nameInput.setAdapter(adapter)
        nameInput.threshold = 1 // Начинать предлагать варианты после ввода первого символа

        // Получение данных из Firebase
        val database = FirebaseDatabase.getInstance()
        val medicationsRef = database.getReference("medications")

        medicationsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                drugsList.clear()
                val names = mutableListOf<String>()
                
                for (drugSnapshot in snapshot.children) {
                    val drug = drugSnapshot.getValue(Drug::class.java)
                    drug?.let {
                        drugsList.add(it)
                        names.add(it.name)
                    }
                }
                
                adapter.clear()
                adapter.addAll(names)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Ошибка загрузки данных: ${error.message}", 
                    Toast.LENGTH_SHORT).show()
            }
        })

        // Обработка выбора препарата
        nameInput.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position) as String
            // Убираем автозаполнение комментария
        }

        // Добавляем TextWatcher для фильтрации в реальном времени
        nameInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.lowercase() ?: ""
                val filteredList = drugsList
                    .filter { it.name.lowercase().contains(query) }
                    .map { it.name }
                adapter.clear()
                adapter.addAll(filteredList)
                adapter.notifyDataSetChanged()
            }
            
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun initializeViews(view: View) {
        nameInput = view.findViewById(R.id.nameInput)
        commentInput = view.findViewById(R.id.et_comment)
        typeSpinner = view.findViewById(R.id.spinner_type)
        frequencySpinner = view.findViewById(R.id.spinner_frequency)
        timesPerDaySpinner = view.findViewById(R.id.spinner_times_per_day)
        saveButton = view.findViewById(R.id.btn_save)
        timeButtonsContainer = view.findViewById(R.id.time_buttons_container)
        addToScheduleButton = view.findViewById(R.id.btn_add_to_schedule)
        daysSelectionContainer = view.findViewById(R.id.days_selection_container)
        dateRangeContainer = view.findViewById(R.id.date_range_container)
        startDateButton = view.findViewById(R.id.start_date_button)
        endDateButton = view.findViewById(R.id.end_date_button)
    }

    private fun setupSpinners() {
        // Типы медикаментов
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.medication_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            typeSpinner.adapter = adapter
        }

        // Настройка спиннера частоты приема
        val frequencyOptions = arrayOf("Ежедневно", "По дням недели", "Только сегодня")
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, frequencyOptions).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            frequencySpinner.adapter = adapter
        }

        // Обработчик выбора частоты
        frequencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> { // Ежедневно
                        daysSelectionContainer.visibility = View.GONE
                        dateRangeContainer.visibility = View.VISIBLE
                    }
                    1 -> { // По дням недели
                        daysSelectionContainer.visibility = View.VISIBLE
                        dateRangeContainer.visibility = View.VISIBLE
                    }
                    2 -> { // Только сегодня
                        daysSelectionContainer.visibility = View.GONE
                        dateRangeContainer.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                daysSelectionContainer.visibility = View.GONE
                dateRangeContainer.visibility = View.GONE
            }
        }

        // Настройка спиннера количества приемов в день
        val timesPerDayOptions = (1..5).map { it.toString() }.toTypedArray()
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, timesPerDayOptions).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            timesPerDaySpinner.adapter = adapter
        }
    }

    private fun setupDateButtons() {
        startDateButton.setOnClickListener {
            showDatePicker(true)
        }

        endDateButton.setOnClickListener {
            showDatePicker(false)
        }
    }

    private fun setupFrequencySpinner() {
        frequencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> { // Ежедневно
                        daysSelectionContainer.visibility = View.GONE
                        dateRangeContainer.visibility = View.VISIBLE
                    }
                    1 -> { // По дням недели
                        daysSelectionContainer.visibility = View.VISIBLE
                        dateRangeContainer.visibility = View.VISIBLE
                    }
                    2 -> { // Только сегодня
                        daysSelectionContainer.visibility = View.GONE
                        dateRangeContainer.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                daysSelectionContainer.visibility = View.GONE
                dateRangeContainer.visibility = View.GONE
            }
        }
    }

    private fun setupTimesPerDaySpinner() {
        timesPerDaySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val times = (position + 1)
                updateTimeButtons(times)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            saveMedication()
        }
    }

    private fun showDatePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                if (isStartDate) {
                    startDate = selectedCalendar
                    startDateButton.text = dateFormat.format(selectedCalendar.time)
                } else {
                    endDate = selectedCalendar
                    endDateButton.text = dateFormat.format(selectedCalendar.time)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun updateTimeButtons(count: Int) {
        timeButtonsContainer.removeAllViews()
        timeSchedules.clear()

        for (i in 1..count) {
            val button = Button(context).apply {
                text = "Выбрать время приема $i"
                setOnClickListener {
                    showTimePickerDialog(i - 1)
                }
            }
            timeButtonsContainer.addView(button)
        }
    }

    private fun showTimePickerDialog(index: Int) {
        val timePickerFragment = DrugSettingTimeFragment().apply {
            setOnTimeSelectedListener { time ->
                val parts = time.split(":")
                val timeSchedule = TimeSchedule(parts[0].toInt(), parts[1].toInt())
                if (index < timeSchedules.size) {
                    timeSchedules[index] = timeSchedule
                } else {
                    timeSchedules.add(timeSchedule)
                }
                updateButtonText(index, parts[0].toInt(), parts[1].toInt())
            }
        }
        timePickerFragment.show(childFragmentManager, "timePicker")
    }

    private fun updateButtonText(index: Int, hour: Int, minute: Int) {
        val button = timeButtonsContainer.getChildAt(index) as Button
        button.text = String.format("%02d:%02d", hour, minute)
    }

    private fun saveMedication() {
        val name = nameInput.text.toString()
        val type = typeSpinner.selectedItem.toString()
        val comment = commentInput.text.toString()
        val frequency = frequencySpinner.selectedItem.toString()
        val timesPerDay = (timesPerDaySpinner.selectedItem.toString()).toIntOrNull() ?: 1

        if (name.isBlank()) {
            Toast.makeText(context, "Введите название медикамента", Toast.LENGTH_SHORT).show()
            return
        }

        if (timeSchedules.size != timesPerDay) {
            Toast.makeText(context, "Пожалуйста, установите время для всех приемов", Toast.LENGTH_SHORT).show()
            return
        }

        val medication = Medication(
            name = name,
            type = type,
            comment = comment,
            frequency = frequency,
            timesPerDay = timesPerDay,
            schedules = timeSchedules.toList()
        )

        MedicationStorage(requireContext()).saveMedication(medication)
        Toast.makeText(context, "Медикамент сохранен", Toast.LENGTH_SHORT).show()
        navigateToSchedule()
    }

    private fun navigateToSchedule() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
