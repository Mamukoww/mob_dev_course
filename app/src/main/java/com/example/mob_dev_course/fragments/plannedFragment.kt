package com.example.mob_dev_course.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mob_dev_course.R
import com.example.mob_dev_course.adapters.ScheduleAdapter
import com.example.mob_dev_course.widgets.SwipeableCalendarLayout
import java.util.*

class PlannedFragment : Fragment() {

    private lateinit var scheduleRecyclerView: RecyclerView
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var calendarContainer: SwipeableCalendarLayout // Кастомный контейнер календаря
    private val scheduleData: MutableMap<Int, List<ScheduleAdapter.ScheduleItem>> = mutableMapOf()
    private val calendar = Calendar.getInstance()
    private var selectedDayIndex: Int = 0 // Хранит выбранный день недели (0 = Пн, ..., 6 = Вс)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.planned, container, false)

        // Найти RecyclerView и контейнер календаря
        scheduleRecyclerView = view.findViewById(R.id.scheduleRecyclerView)
        calendarContainer = view.findViewById(R.id.week_calendar)

        // Инициализация RecyclerView
        scheduleAdapter = ScheduleAdapter(emptyList())
        scheduleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        scheduleRecyclerView.adapter = scheduleAdapter

        // Заполнение тестовых данных
        populateTestSchedule()

        // Инициализация календаря
        initializeCalendar(view)

        // Обработка свайпов через SwipeableCalendarLayout
        calendarContainer.setSwipeListener(object : SwipeableCalendarLayout.SwipeListener {
            override fun onSwipeLeft() {
                calendar.add(Calendar.WEEK_OF_YEAR, 1) // Следующая неделя
                updateWeek(view)
            }

            override fun onSwipeRight() {
                calendar.add(Calendar.WEEK_OF_YEAR, -1) // Предыдущая неделя
                updateWeek(view)
            }
        })

        return view
    }

    // Инициализация календаря
    private fun initializeCalendar(view: View) {
        updateWeek(view)
        highlightToday(view)
    }

    // Обновление текущей недели
    private fun updateWeek(view: View) {
        val days = listOf(
            view.findViewById<TextView>(R.id.day_1),
            view.findViewById<TextView>(R.id.day_2),
            view.findViewById<TextView>(R.id.day_3),
            view.findViewById<TextView>(R.id.day_4),
            view.findViewById<TextView>(R.id.day_5),
            view.findViewById<TextView>(R.id.day_6),
            view.findViewById<TextView>(R.id.day_7)
        )

        // Установить дату понедельника текущей недели
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        for (i in days.indices) {
            val dayInMonth = calendar.get(Calendar.DAY_OF_MONTH)
            days[i].text = "${getDayName(i)}\n$dayInMonth"

            // Сбросить выделение для всех дней
            resetDayHighlight(days[i])

            // Установить обработчик нажатия
            days[i].setOnClickListener {
                onDaySelected(i, days)
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Вернуться к началу текущей недели
        calendar.add(Calendar.DAY_OF_MONTH, -7)

        // Выделить ранее выбранный день недели
        highlightDay(days[selectedDayIndex])

        // Обновить расписание для выбранного дня
        updateScheduleForDay(selectedDayIndex)
    }

    // Метод для выделения сегодняшнего дня
    private fun highlightToday(view: View) {
        val today = Calendar.getInstance()
        val days = listOf(
            view.findViewById<TextView>(R.id.day_1),
            view.findViewById<TextView>(R.id.day_2),
            view.findViewById<TextView>(R.id.day_3),
            view.findViewById<TextView>(R.id.day_4),
            view.findViewById<TextView>(R.id.day_5),
            view.findViewById<TextView>(R.id.day_6),
            view.findViewById<TextView>(R.id.day_7)
        )

        // Установить дату понедельника текущей недели
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        // Сначала сбрасываем выделение со всех дней
        days.forEach { resetDayHighlight(it) }

        // Проверяем каждый день недели
        for (i in days.indices) {
            val dayInMonth = calendar.get(Calendar.DAY_OF_MONTH)
            if (today.get(Calendar.DAY_OF_MONTH) == dayInMonth &&
                today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
            ) {
                // Выделяем сегодняшний день
                highlightDay(days[i])
                selectedDayIndex = i
                updateScheduleForDay(selectedDayIndex)
                break
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }


    // Обработка выбора дня
    private fun onDaySelected(dayIndex: Int, days: List<TextView>) {
        // Снять выделение со всех дней
        days.forEach { resetDayHighlight(it) }

        // Выделить выбранный день
        highlightDay(days[dayIndex])

        // Обновить выбранный день
        selectedDayIndex = dayIndex

        // Обновить расписание для выбранного дня
        updateScheduleForDay(dayIndex)
    }

    // Выделить день
    private fun highlightDay(dayView: TextView) {
        dayView.setTextColor(resources.getColor(R.color.blue, null))
        dayView.setTypeface(null, android.graphics.Typeface.BOLD)
    }

    // Сбросить выделение дня
    private fun resetDayHighlight(dayView: TextView) {
        dayView.setTextColor(resources.getColor(R.color.black, null))
        dayView.setTypeface(null, android.graphics.Typeface.NORMAL)
    }

    // Обновление расписания для выбранного дня
    private fun updateScheduleForDay(dayIndex: Int) {
        val newSchedule = scheduleData[dayIndex] ?: emptyList()
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
    }

    // Получение названия дня недели
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
