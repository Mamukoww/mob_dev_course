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
import com.example.mob_dev_course.adapters.ScheduleItem
import com.example.mob_dev_course.widgets.SwipeableCalendarLayout
import com.example.mob_dev_course.data.ScheduleStorage
import com.example.mob_dev_course.data.ScheduleData
import com.example.mob_dev_course.data.MedicationStatus
import java.util.*

class PlannedFragment : Fragment() {

    private lateinit var scheduleRecyclerView: RecyclerView
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var calendarContainer: SwipeableCalendarLayout
    private val calendar = Calendar.getInstance()
    private var selectedDate: Long = calendar.timeInMillis

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.planned, container, false)

        context?.let { ScheduleStorage.initialize(it) }

        scheduleRecyclerView = view.findViewById(R.id.scheduleRecyclerView)
        calendarContainer = view.findViewById(R.id.week_calendar)

        scheduleAdapter = ScheduleAdapter(emptyList(), childFragmentManager)
        scheduleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        scheduleRecyclerView.adapter = scheduleAdapter

        initializeCalendar(view)
        loadScheduleForSelectedDate()

        calendarContainer.setSwipeListener(object : SwipeableCalendarLayout.SwipeListener {
            override fun onSwipeLeft() {
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
                updateWeek(view)
            }

            override fun onSwipeRight() {
                calendar.add(Calendar.WEEK_OF_YEAR, -1)
                updateWeek(view)
            }
        })

        return view
    }

    private fun initializeCalendar(view: View) {
        updateWeek(view)
        highlightToday(view)
    }

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

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val weekStart = calendar.timeInMillis

        for (i in days.indices) {
            val dayInMonth = calendar.get(Calendar.DAY_OF_MONTH)
            days[i].text = "${getDayName(i)}\n$dayInMonth"
            unhighlightDay(days[i])

            val currentDate = calendar.timeInMillis
            days[i].setOnClickListener {
                onDateSelected(currentDate, days)
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        calendar.timeInMillis = weekStart
        
        // Выделить выбранную дату, если она в текущей неделе
        val selectedCalendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
        val currentWeekCalendar = Calendar.getInstance().apply { timeInMillis = weekStart }
        
        if (selectedCalendar.get(Calendar.WEEK_OF_YEAR) == currentWeekCalendar.get(Calendar.WEEK_OF_YEAR) &&
            selectedCalendar.get(Calendar.YEAR) == currentWeekCalendar.get(Calendar.YEAR)) {
            val dayIndex = (selectedCalendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
            highlightDay(days[dayIndex])
        }
    }

    private fun onDateSelected(date: Long, days: List<TextView>) {
        selectedDate = date
        days.forEach { unhighlightDay(it) }
        val selectedCalendar = Calendar.getInstance().apply { timeInMillis = date }
        val dayIndex = (selectedCalendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
        highlightDay(days[dayIndex])
        loadScheduleForSelectedDate()
    }

    private fun highlightToday(view: View) {
        val today = Calendar.getInstance()
        val weekStart = calendar.clone() as Calendar
        weekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        
        if (today.get(Calendar.WEEK_OF_YEAR) == weekStart.get(Calendar.WEEK_OF_YEAR) &&
            today.get(Calendar.YEAR) == weekStart.get(Calendar.YEAR)) {
            val dayIndex = (today.get(Calendar.DAY_OF_WEEK) + 5) % 7
            val dayView = when (dayIndex) {
                0 -> view.findViewById<TextView>(R.id.day_1)
                1 -> view.findViewById<TextView>(R.id.day_2)
                2 -> view.findViewById<TextView>(R.id.day_3)
                3 -> view.findViewById<TextView>(R.id.day_4)
                4 -> view.findViewById<TextView>(R.id.day_5)
                5 -> view.findViewById<TextView>(R.id.day_6)
                else -> view.findViewById<TextView>(R.id.day_7)
            }
            highlightDay(dayView)
        }
    }

    private fun highlightDay(textView: TextView) {
        textView.setBackgroundResource(R.drawable.selected_day_background)
    }

    private fun unhighlightDay(textView: TextView) {
        textView.setBackgroundResource(0)
    }

    private fun getDayName(index: Int): String {
        return when (index) {
            0 -> "ПН"
            1 -> "ВТ"
            2 -> "СР"
            3 -> "ЧТ"
            4 -> "ПТ"
            5 -> "СБ"
            else -> "ВС"
        }
    }

    private fun loadScheduleForSelectedDate() {
        val schedules = ScheduleStorage.getScheduleForDate(selectedDate)
        val scheduleItems = schedules.map { schedule ->
            // Разбираем строку времени на часы и минуты
            val timeParts = schedule.time.split(":")
            val timeFormatted = if (timeParts.size == 2) {
                schedule.time // Используем как есть, если уже в нужном формате
            } else {
                String.format("%02d:%02d", 0, 0) // Значение по умолчанию, если формат неверный
            }
            
            ScheduleItem(
                id = schedule.id,
                time = timeFormatted,
                title = schedule.medicationName,
                description = schedule.description,
                status = schedule.status
            )
        }.sortedBy { it.time }

        scheduleAdapter.updateData(scheduleItems)
    }
}