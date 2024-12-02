package com.example.mob_dev_course.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.mob_dev_course.R

class DrugSettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Инфлейт макета для этого фрагмента
        return inflater.inflate(R.layout.drug_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Подключение элементов интерфейса
        val spinnerType: Spinner = view.findViewById(R.id.spinner_type)
        val spinnerFrequency: Spinner = view.findViewById(R.id.spinner_frequency)
        val spinnerTimesPerDay: Spinner = view.findViewById(R.id.spinner_times_per_day)
        val btnSave: Button = view.findViewById(R.id.btn_save)
        val btnAddToSchedule: Button = view.findViewById(R.id.btn_add_to_schedule)

        // Настройка адаптеров для Spinner
        val typeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Лекарство", "Витамин", "БАД")
        )
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = typeAdapter

        val frequencyAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Единоразово", "Через день", "Каждый день")
        )
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFrequency.adapter = frequencyAdapter

        val timesAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            (1..5).map { it.toString() }
        )
        timesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTimesPerDay.adapter = timesAdapter

        // Настройка обработчиков кнопок
        btnSave.setOnClickListener {
            // Добавьте вашу логику для сохранения
        }

        btnAddToSchedule.setOnClickListener {
            // Добавьте вашу логику для добавления в расписание
        }
    }
}