package com.example.mob_dev_course.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mob_dev_course.R
import com.example.mob_dev_course.data.Medication
import com.example.mob_dev_course.data.MedicationStorage
import com.example.mob_dev_course.data.TimeSchedule
import java.util.*

class DrugSettingsFragment : Fragment() {
    private lateinit var medicationStorage: MedicationStorage
    private lateinit var nameInput: EditText
    private lateinit var typeSpinner: Spinner
    private lateinit var commentInput: EditText
    private lateinit var frequencySpinner: Spinner
    private lateinit var timesPerDaySpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var timeButtonsContainer: LinearLayout
    
    private val timeSchedules = mutableListOf<TimeSchedule>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.drug_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        medicationStorage = MedicationStorage(requireContext())

        // Инициализация views
        nameInput = view.findViewById(R.id.et_name)
        typeSpinner = view.findViewById(R.id.spinner_type)
        commentInput = view.findViewById(R.id.et_comment)
        frequencySpinner = view.findViewById(R.id.spinner_frequency)
        timesPerDaySpinner = view.findViewById(R.id.spinner_times_per_day)
        saveButton = view.findViewById(R.id.btn_save)
        timeButtonsContainer = view.findViewById(R.id.time_buttons_container)

        // Настройка спиннеров
        setupSpinners()

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

        // Частота приема
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.frequency_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            frequencySpinner.adapter = adapter
        }

        // Количество приемов в день
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.times_per_day,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
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
        button.text = String.format("Прием %d: %02d:%02d", index + 1, hour, minute)
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

        medicationStorage.saveMedication(medication)
        Toast.makeText(context, "Медикамент сохранен", Toast.LENGTH_SHORT).show()
        activity?.onBackPressed()
    }
}
