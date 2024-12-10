package com.example.mob_dev_course.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.mob_dev_course.R

class MedicationDetailsDialog : DialogFragment() {

    interface MedicationDetailsListener {
        fun onEdit()
        fun onDelete()
    }

    private var listener: MedicationDetailsListener? = null

    fun setListener(listener: MedicationDetailsListener) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.detailed_medications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем аргументы
        val title = arguments?.getString("title") ?: "Название"
        val description = arguments?.getString("description") ?: "Lorem ipsum dolor sit amet,\nconsectetur adipiscing elit"

        // Настраиваем UI
        view.findViewById<TextView>(R.id.titleText).text = title
        view.findViewById<TextView>(R.id.descriptionText).text = description

        // Настраиваем кнопки
        view.findViewById<TextView>(R.id.editButton).setOnClickListener {
            listener?.onEdit()
            dismiss()
        }

        view.findViewById<TextView>(R.id.deleteButton).setOnClickListener {
            listener?.onDelete()
            dismiss()
        }
    }

    companion object {
        fun newInstance(title: String, description: String): MedicationDetailsDialog {
            return MedicationDetailsDialog().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                    putString("description", description)
                }
            }
        }
    }
}