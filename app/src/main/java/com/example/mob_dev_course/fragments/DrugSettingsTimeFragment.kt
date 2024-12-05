package com.example.mob_dev_course.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.example.mob_dev_course.R
import java.util.Calendar

class DrugSettingTimeFragment : DialogFragment() {
    private lateinit var hourPicker: NumberPicker
    private lateinit var minutePicker: NumberPicker
    private lateinit var secondPicker: NumberPicker
    private lateinit var saveButton: Button

    private var onTimeSelectedListener: ((String) -> Unit)? = null

    fun setOnTimeSelectedListener(listener: (String) -> Unit) {
        onTimeSelectedListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.drug_setiing_time, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar = Calendar.getInstance()
        hourPicker = view.findViewById(R.id.hourPicker)
        minutePicker = view.findViewById(R.id.minutePicker)
        secondPicker = view.findViewById(R.id.secondPicker)
        saveButton = view.findViewById(R.id.saveButton)

        setupPickers(calendar)
        setupSaveButton()
    }

    private fun setupPickers(calendar: Calendar) {
        hourPicker.apply {
            minValue = 0
            maxValue = 23
            value = calendar.get(Calendar.HOUR_OF_DAY)
        }

        minutePicker.apply {
            minValue = 0
            maxValue = 59
            value = calendar.get(Calendar.MINUTE)
        }

        secondPicker.apply {
            minValue = 0
            maxValue = 59
            value = calendar.get(Calendar.SECOND)
        }
    }

    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            val selectedTime = "${hourPicker.value}:${minutePicker.value}:${secondPicker.value}"
            onTimeSelectedListener?.invoke(selectedTime)
            dismiss()
        }
    }
}