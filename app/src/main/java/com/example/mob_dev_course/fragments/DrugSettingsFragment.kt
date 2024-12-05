package com.example.mob_dev_course.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mob_dev_course.R
import com.example.mob_dev_course.data.MedicationsRepository
import com.example.mob_dev_course.models.Medication

class DrugSettingsFragment : Fragment() {
    private lateinit var nameInput: EditText
    private lateinit var typeSpinner: Spinner
    private lateinit var frequencySpinner: Spinner
    private lateinit var timesPerDaySpinner: Spinner
    private lateinit var commentInput: EditText
    private lateinit var saveButton: Button
    private lateinit var addToScheduleButton: Button
    private lateinit var timeButtonsContainer: LinearLayout
    private val selectedTimes = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.drug_settings, container, false)

        // Инициализация views
        nameInput = view.findViewById(R.id.et_name)
        typeSpinner = view.findViewById(R.id.spinner_type)
        frequencySpinner = view.findViewById(R.id.spinner_frequency)
        timesPerDaySpinner = view.findViewById(R.id.spinner_times_per_day)
        commentInput = view.findViewById(R.id.et_comment)
        saveButton = view.findViewById(R.id.btn_save)
        addToScheduleButton = view.findViewById(R.id.btn_add_to_schedule)
        timeButtonsContainer = view.findViewById(R.id.time_buttons_container)

        // Настройка спиннеров
        setupSpinners()

        timesPerDaySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val times = position + 1 // Assuming spinner values start from 1
                createButtons(times)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        saveButton.setOnClickListener {
            saveMedication()
        }

        addToScheduleButton.setOnClickListener {
            // TODO: Добавить в расписание
            Toast.makeText(context, "Функция будет добавлена позже", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun setupSpinners() {
        // Типы лекарств
        val types = arrayOf("Таблетки", "Капсулы", "Сироп", "Капли", "Другое")
        val typeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = typeAdapter

        // Частота приема
        val frequencies = arrayOf("Ежедневно", "По дням недели", "По необходимости")
        val frequencyAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, frequencies)
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        frequencySpinner.adapter = frequencyAdapter

        // Количество раз в день
        val timesPerDay = Array(5) { (it + 1).toString() }
        val timesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, timesPerDay)
        timesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timesPerDaySpinner.adapter = timesAdapter
    }

    private fun createButtons(count: Int) {
        timeButtonsContainer.removeAllViews()
        selectedTimes.clear()

        for (i in 1..count) {
            val button = Button(context).apply {
                text = "Установить время для дозы $i"
                setOnClickListener {
                    openTimePicker(i - 1, this)
                }
            }
            timeButtonsContainer.addView(button)
        }
    }

    private fun openTimePicker(index: Int, button: Button) {
        val fragment = DrugSettingTimeFragment()
        fragment.show(childFragmentManager, "timePicker")

        fragment.setOnTimeSelectedListener { time ->
            if (selectedTimes.size > index) {
                selectedTimes[index] = time
            } else {
                selectedTimes.add(time)
            }
            button.text = time
        }
    }

    private fun saveMedication() {
        val name = nameInput.text.toString()
        val type = typeSpinner.selectedItem.toString()
        val frequency = frequencySpinner.selectedItem.toString()
        val timesPerDay = timesPerDaySpinner.selectedItem.toString()
        val comment = commentInput.text.toString()

        if (name.isEmpty()) {
            Toast.makeText(context, "Пожалуйста, введите название препарата", Toast.LENGTH_SHORT).show()
            return
        }

        val medication = Medication(
            name = name,
            dosage = type,
            frequency = frequency,
            time = selectedTimes.joinToString(", "),
            startDate = "",
            endDate = "",
            notes = comment
        )

        MedicationsRepository.addMedication(medication)
        Toast.makeText(context, "Лекарство успешно добавлено", Toast.LENGTH_SHORT).show()

        // Возврат к предыдущему экрану
        activity?.onBackPressed()
    }
}
