package com.example.mob_dev_course.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mob_dev_course.R
import com.example.mob_dev_course.adapters.ScheduleAdapter
import java.util.*


class PlannedFragment : Fragment() {

    private lateinit var scheduleRecyclerView: RecyclerView
    private lateinit var scheduleAdapter: ScheduleAdapter
    private val scheduleData: MutableMap<Int, List<ScheduleAdapter.ScheduleItem>> = mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.planned, container, false)

        // Инициализация RecyclerView
        scheduleRecyclerView = view.findViewById(R.id.scheduleRecyclerView)
        scheduleAdapter = ScheduleAdapter(emptyList())
        scheduleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        scheduleRecyclerView.adapter = scheduleAdapter

        // Заполнение тестовых данных
        populateTestSchedule()

        // Найти элементы для дней недели
        val days = listOf(
            view.findViewById<TextView>(R.id.day_1),
            view.findViewById<TextView>(R.id.day_2),
            view.findViewById<TextView>(R.id.day_3),
            view.findViewById<TextView>(R.id.day_4),
            view.findViewById<TextView>(R.id.day_5),
            view.findViewById<TextView>(R.id.day_6),
            view.findViewById<TextView>(R.id.day_7)
        )

        // Получить текущую дату
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Android: Воскресенье = 1, Понедельник = 2 и т.д.
        val startOfWeek = calendar.apply {
            add(Calendar.DAY_OF_MONTH, -(dayOfWeek - Calendar.MONDAY))
        }

        // Установить текст для каждого дня недели
        for (i in days.indices) {
            val dayInMonth = startOfWeek.get(Calendar.DAY_OF_MONTH)
            days[i].text = "${getDayName(i)}\n$dayInMonth"

            // Установить обработчик нажатия
            days[i].setOnClickListener {
                onDaySelected(i, dayInMonth, days)
            }

            // Если это текущий день, выделяем его
            if (dayInMonth == today) {
                highlightDay(days[i])
                updateScheduleForDay(i) // Обновить расписание для текущего дня
            }

            // Переход к следующему дню
            startOfWeek.add(Calendar.DAY_OF_MONTH, 1)
        }

        return view
    }

    // Обработка выбора дня
    // Обработка выбора дня
    private fun onDaySelected(dayIndex: Int, dayInMonth: Int, days: List<TextView>) {
        // Снять выделение со всех дней
        days.forEach { resetDayHighlight(it) }

        // Выделить выбранный день
        highlightDay(days[dayIndex])

        // Обновить список расписания
        updateScheduleForDay(dayIndex)
    }


    // Выделить день
    private fun highlightDay(dayView: TextView) {
        dayView.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
        dayView.setTypeface(null, android.graphics.Typeface.BOLD)
    }

    // Сбросить выделение дня
    private fun resetDayHighlight(dayView: TextView) {
        dayView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        dayView.setTypeface(null, android.graphics.Typeface.NORMAL)
    }

    // Обновить расписание для выбранного дня
    private fun updateScheduleForDay(dayIndex: Int) {
        val newSchedule = scheduleData[dayIndex] ?: emptyList() // Получить данные для дня или пустой список
        scheduleAdapter.updateData(newSchedule)
    }

    // Заполнение тестовых данных
    private fun populateTestSchedule() {
        scheduleData[0] = listOf(
            ScheduleAdapter.ScheduleItem("9:00", "Лекарство A", "Принять перед едой"),
            ScheduleAdapter.ScheduleItem("12:00", "Лекарство B", "Принять после еды")
        )

        scheduleData[1] = listOf(
            ScheduleAdapter.ScheduleItem("10:00", "Лекарство C", "Принять с водой")
        )

        scheduleData[2] = listOf(
            ScheduleAdapter.ScheduleItem("8:00", "Лекарство D", "Принять натощак"),
            ScheduleAdapter.ScheduleItem("18:00", "Лекарство E", "Принять после ужина")
        )

        scheduleData[3] = listOf(
            ScheduleAdapter.ScheduleItem("9:30", "Лекарство F", "Принять перед прогулкой")
        )

        scheduleData[4] = listOf(
            ScheduleAdapter.ScheduleItem("11:00", "Лекарство G", "Принять после завтрака"),
            ScheduleAdapter.ScheduleItem("15:00", "Лекарство H", "Принять с чаем")
        )

        scheduleData[5] = listOf(
            ScheduleAdapter.ScheduleItem("10:00", "Лекарство I", "Принять перед сном")
        )

        scheduleData[6] = listOf(
            ScheduleAdapter.ScheduleItem("8:30", "Лекарство J", "Принять с фруктами")
        )
    }




    // Метод для получения названия дня недели
    private fun getDayName(index: Int): String {
        return when (index) {
            0 -> "Пн"
            1 -> "Вт"
            2 -> "Ср"
            3 -> "Чт"
            4 -> "Пт"
            5 -> "Сб"
            6 -> "Вс"
            else -> ""
        }
    }
}
