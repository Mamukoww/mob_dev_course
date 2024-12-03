package com.example.mob_dev_course.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import com.example.mob_dev_course.R

class DrugSettingTimeFragment : Fragment() {
    private lateinit var hourPicker: NumberPicker
    private lateinit var minutePicker: NumberPicker
    private lateinit var secondPicker: NumberPicker
    private lateinit var saveButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.drug_setiing_time, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hourPicker = view.findViewById(R.id.hourPicker)
        minutePicker = view.findViewById(R.id.minutePicker)
        secondPicker = view.findViewById(R.id.secondPicker)
        saveButton = view.findViewById(R.id.saveButton)

        setupPickers()
        setupSaveButton()
    }

    private fun setupPickers() {
        hourPicker.apply {
            minValue = 0
            maxValue = 23
            value = 6
        }

        minutePicker.apply {
            minValue = 0
            maxValue = 59
            value = 27
        }

        secondPicker.apply {
            minValue = 0
            maxValue = 59
            value = 54
        }
    }

    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            // Здесь будет логика сохранения времени
            val hour = hourPicker.value
            val minute = minutePicker.value
            val second = secondPicker.value
            // TODO: Добавить обработку сохранения
        }
    }
}