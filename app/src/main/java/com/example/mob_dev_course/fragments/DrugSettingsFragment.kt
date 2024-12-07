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
import java.util.*
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class DrugSettingsFragment : Fragment() {
    private lateinit var nameInput: EditText
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.drug_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация ScheduleStorage
        context?.let { ScheduleStorage.initialize(it) }

        initializeViews(view)
        setupSpinners()
        setupDateButtons()
        setupAddToScheduleButton()

        // Обработчик изменения количества приемов
        timesPerDaySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val times = (position + 1)
                updateTimeButtons(times)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Обработчик кнопки сохранения
        saveButton.setOnClickListener {
            saveMedication()
        }
    }

    private fun initializeViews(view: View) {
        nameInput = view.findViewById(R.id.et_name)
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

        savedMedicationId = medication.id // Сохраняем ID лекарства
        MedicationStorage(requireContext()).saveMedication(medication)
        Toast.makeText(context, "Медикамент сохранен", Toast.LENGTH_SHORT).show()
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
