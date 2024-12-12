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
import com.example.mob_dev_course.utils.NotificationScheduler
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
    private var savedMedicationId: String? = null
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
        setupAddToScheduleButton()
    }

    private fun setupAutoComplete() {
        // Инициализация адаптера
        adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf<String>()
        )
        nameInput.setAdapter(adapter)
        nameInput.threshold = 1

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

        // Добавляем TextWatcher для фильтрации в реальном времени
        nameInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.lowercase() ?: ""
                if (query.isNotEmpty()) {
                    val filteredList = drugsList
                        .filter { it.name.lowercase().contains(query) }
                        .map { it.name }
                    adapter.clear()
                    adapter.addAll(filteredList)
                    adapter.notifyDataSetChanged()
                    if (filteredList.isNotEmpty()) {
                        nameInput.showDropDown()
                    }
                }
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

    private fun setupDateButtons() {
        startDateButton.setOnClickListener {
            showDatePicker(true)
        }

        endDateButton.setOnClickListener {
            showDatePicker(false)
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

    private fun setupSpinners() {
        // Типы медикаментов
        val typeAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.medication_types,
            R.layout.spinner_dropdown_item
        )
        typeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        typeSpinner.adapter = typeAdapter

        // Настройка спиннера частоты приема
        val frequencyOptions = arrayOf("Ежедневно", "По дням недели", "Только сегодня")
        val frequencyAdapter = ArrayAdapter(requireContext(), R.layout.spinner_dropdown_item, frequencyOptions)
        frequencyAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        frequencySpinner.adapter = frequencyAdapter

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

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Настройка спиннера количества приемов в день
        val timesPerDayOptions = (1..5).map { it.toString() }.toTypedArray()
        val timesPerDayAdapter = ArrayAdapter(requireContext(), R.layout.spinner_dropdown_item, timesPerDayOptions)
        timesPerDayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        timesPerDaySpinner.adapter = timesPerDayAdapter
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

    private fun updateTimeButtons(count: Int) {
        timeButtonsContainer.removeAllViews()
        timeSchedules.clear()

        for (i in 1..count) {
            val button = androidx.appcompat.widget.AppCompatButton(requireContext()).apply {
                text = "Выбрать время приема $i"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    height = resources.getDimensionPixelSize(R.dimen.button_height)
                    bottomMargin = 16
                }
                textSize = 20f
                setTextColor(resources.getColor(R.color.white))
                background = resources.getDrawable(R.drawable.rounded_button)
                setOnClickListener {
                    showTimePickerDialog(i - 1)
                }
            }
            timeButtonsContainer.addView(button)
        }
    }

    private fun updateButtonText(index: Int, hour: Int, minute: Int) {
        val button = timeButtonsContainer.getChildAt(index) as Button
        val formattedHour = String.format("%02d", hour)
        val formattedMinute = String.format("%02d", minute)
        button.text = "Время приема $formattedHour:$formattedMinute"
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

    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            saveMedication()
        }
    }

    private fun saveMedication() {
        val name = nameInput.text.toString().trim()
        val type = typeSpinner.selectedItem.toString()
        val comment = commentInput.text.toString().trim()
        val frequency = frequencySpinner.selectedItem.toString()
        val timesPerDay = (timesPerDaySpinner.selectedItem.toString()).toIntOrNull() ?: 1

        // Проверки
        if (name.isBlank()) {
            Toast.makeText(context, "Введите название медикамента", Toast.LENGTH_SHORT).show()
            return
        }

        if (timeSchedules.size != timesPerDay) {
            Toast.makeText(context, "Пожалуйста, установите время для всех приемов", Toast.LENGTH_SHORT).show()
            return
        }

        // Проверка дат для ежедневного приема и приема по дням недели
        if (frequency != "Только сегодня" && dateRangeContainer.visibility == View.VISIBLE) {
            if (startDate == null || endDate == null) {
                Toast.makeText(context, "Пожалуйста, укажите период приема", Toast.LENGTH_SHORT).show()
                return
            }
            if (endDate?.before(startDate) == true) {
                Toast.makeText(context, "Дата окончания не может быть раньше даты начала", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Проверка выбранных дней недели для еженедельного приема
        if (frequency == "По дням недели" && daysSelectionContainer.visibility == View.VISIBLE) {
            val selectedDays = getSelectedDays()
            if (selectedDays.isEmpty()) {
                Toast.makeText(context, "Пожалуйста, выберите дни приема", Toast.LENGTH_SHORT).show()
                return
            }
        }

        try {
            val medication = Medication(
                name = name,
                type = type,
                comment = comment,
                frequency = frequency,
                timesPerDay = timesPerDay,
                schedules = timeSchedules.toList()
            )

            val medicationStorage = MedicationStorage(requireContext())
            medicationStorage.saveMedication(medication)
            savedMedicationId = medication.id

            // Планирование уведомлений
            val notificationScheduler = NotificationScheduler
            timeSchedules.forEach { schedule ->
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, schedule.hour)
                    set(Calendar.MINUTE, schedule.minute)
                    set(Calendar.SECOND, 0)
                }
                notificationScheduler.scheduleMedicationReminder(
                    requireContext(),
                    medication.id,
                    name,
                    calendar.timeInMillis,
                    frequency,
                    timesPerDay
                )
            }

            Toast.makeText(context, "Медикамент успешно сохранен", Toast.LENGTH_SHORT).show()
            // Очищаем поля после сохранения
            nameInput.text.clear()
            commentInput.text.clear()
            timeSchedules.clear()
            timeButtonsContainer.removeAllViews()
            startDate = null
            endDate = null
            startDateButton.text = "Дата начала"
            endDateButton.text = "Дата окончания"
            
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка при сохранении: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAddToScheduleButton() {
        addToScheduleButton.setOnClickListener {
            val medicationName = nameInput.text.toString()
            val frequency = frequencySpinner.selectedItem.toString()
            val timesPerDay = (timesPerDaySpinner.selectedItem.toString()).toIntOrNull() ?: 1
            val selectedTimes = getSelectedTimes()
            
            // Сначала сохраняем препарат в medications
            saveMedication()
            
            // Проверяем, что у нас есть ID лекарства
            if (savedMedicationId == null) {
                Toast.makeText(context, "Ошибка сохранения лекарства", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Затем добавляем в расписание
            when (frequencySpinner.selectedItemPosition) {
                0 -> { // Ежедневно
                    addDailySchedule(medicationName, selectedTimes, frequency, timesPerDay)
                }
                1 -> { // По дням недели
                    addWeeklySchedule(medicationName, selectedTimes, frequency, timesPerDay)
                }
                2 -> { // Только сегодня
                    addTodaySchedule(medicationName, selectedTimes, frequency, timesPerDay)
                }
            }
            
            Toast.makeText(context, "Добавлено в расписание и сохранено", Toast.LENGTH_SHORT).show()
            navigateToSchedule()
        }
    }

    private fun addDailySchedule(medicationName: String, times: List<String>, frequency: String, timesPerDay: Int) {
        if (startDate == null || endDate == null) {
            Toast.makeText(context, "Выберите период приема", Toast.LENGTH_SHORT).show()
            return
        }

        val currentDate = startDate!!.clone() as Calendar
        while (!currentDate.after(endDate)) {
            times.forEach { time ->
                val scheduleData = ScheduleData(
                    medicationId = savedMedicationId!!,
                    medicationName = medicationName,
                    time = time,
                    description = commentInput.text.toString(),
                    date = currentDate.timeInMillis,
                    frequency = frequency,
                    timesPerDay = timesPerDay,
                    startDate = startDate?.timeInMillis,
                    endDate = endDate?.timeInMillis
                )
                ScheduleStorage.saveSchedule(scheduleData)
            }
            currentDate.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun addWeeklySchedule(medicationName: String, times: List<String>, frequency: String, timesPerDay: Int) {
        if (startDate == null || endDate == null) {
            Toast.makeText(context, "Выберите период приема", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedDays = getSelectedDays()
        val currentDate = startDate!!.clone() as Calendar
        
        while (!currentDate.after(endDate)) {
            val dayOfWeek = (currentDate.get(Calendar.DAY_OF_WEEK) + 5) % 7
            if (selectedDays.contains(dayOfWeek)) {
                times.forEach { time ->
                    val scheduleData = ScheduleData(
                        medicationId = savedMedicationId!!,
                        medicationName = medicationName,
                        time = time,
                        description = commentInput.text.toString(),
                        date = currentDate.timeInMillis,
                        frequency = frequency,
                        timesPerDay = timesPerDay,
                        startDate = startDate?.timeInMillis,
                        endDate = endDate?.timeInMillis
                    )
                    ScheduleStorage.saveSchedule(scheduleData)
                }
            }
            currentDate.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun addTodaySchedule(medicationName: String, times: List<String>, frequency: String, timesPerDay: Int) {
        val today = Calendar.getInstance()
        times.forEach { time ->
            val scheduleData = ScheduleData(
                medicationId = savedMedicationId!!,
                medicationName = medicationName,
                time = time,
                description = commentInput.text.toString(),
                date = today.timeInMillis,
                frequency = frequency,
                timesPerDay = timesPerDay
            )
            ScheduleStorage.saveSchedule(scheduleData)
        }
    }

    private fun getSelectedDays(): List<Int> {
        val selectedDays = mutableListOf<Int>()
        val daysCheckboxes = listOf(
            R.id.monday to 0,
            R.id.tuesday to 1,
            R.id.wednesday to 2,
            R.id.thursday to 3,
            R.id.friday to 4,
            R.id.saturday to 5,
            R.id.sunday to 6
        )
        
        daysCheckboxes.forEach { (checkboxId, dayIndex) ->
            if (view?.findViewById<CheckBox>(checkboxId)?.isChecked == true) {
                selectedDays.add(dayIndex)
            }
        }
        
        return if (selectedDays.isEmpty()) listOf(0, 1, 2, 3, 4, 5, 6) else selectedDays
    }

    private fun getSelectedTimes(): List<String> {
        // Получаем все выбранные времена из time_buttons_container
        val timesList = mutableListOf<String>()
        timeButtonsContainer.children.toList().forEach { view ->
            if (view is Button) {
                timesList.add(view.text.toString())
            }
        }
        return timesList
    }

    private fun navigateToSchedule() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}