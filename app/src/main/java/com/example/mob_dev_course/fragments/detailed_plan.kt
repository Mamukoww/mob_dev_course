package com.example.mob_dev_course.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.mob_dev_course.R

class DetailedPlan : DialogFragment() {

    interface DetailedPlanListener {
        fun onSkip()
        fun onTake()
        fun onCancel()
    }

    private var listener: DetailedPlanListener? = null

    fun setListener(listener: DetailedPlanListener) {
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
        return inflater.inflate(R.layout.detailed_plan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем аргументы
        val title = arguments?.getString("title") ?: "Название"
        val description = arguments?.getString("description") ?: "Lorem ipsum dolor sit amet,\nconsectetur adipiscing elit"
        val time = arguments?.getString("time") ?: "11:11"

        // Настраиваем UI
        view.findViewById<TextView>(R.id.titleText).text = title
        view.findViewById<TextView>(R.id.descriptionText).text = description
        view.findViewById<TextView>(R.id.scheduledTimeText).text = "Запланировано на $time"

        // Настраиваем кнопки
        view.findViewById<TextView>(R.id.skipButton).setOnClickListener {
            listener?.onSkip()
            dismiss()
        }

        view.findViewById<TextView>(R.id.takeButton).setOnClickListener {
            listener?.onTake()
            dismiss()
        }

        view.findViewById<TextView>(R.id.cancelButton).setOnClickListener {
            listener?.onCancel()
            dismiss()
        }
    }

    companion object {
        fun newInstance(title: String, description: String, time: String): DetailedPlan {
            return DetailedPlan().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                    putString("description", description)
                    putString("time", time)
                }
            }
        }
    }
}