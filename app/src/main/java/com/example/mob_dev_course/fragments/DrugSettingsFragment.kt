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

        // Настройка спиннеров
        setupSpinners()

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
        val timesPerDay = Array(10) { (it + 1).toString() }
        val timesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, timesPerDay)
        timesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timesPerDaySpinner.adapter = timesAdapter
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
            time = timesPerDay,
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
