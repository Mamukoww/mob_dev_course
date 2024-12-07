package com.example.mob_dev_course.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.content.Context
import android.content.SharedPreferences
import java.util.Calendar

object ScheduleStorage {
    private const val PREFS_NAME = "schedule_prefs"
    private const val SCHEDULE_KEY = "schedule_data"
    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveSchedule(scheduleData: ScheduleData) {
        val scheduleList = getAllSchedules().toMutableList()
        scheduleList.add(scheduleData)
        val json = gson.toJson(scheduleList)
        prefs.edit().putString(SCHEDULE_KEY, json).apply()
    }

    fun getAllSchedules(): List<ScheduleData> {
        val json = prefs.getString(SCHEDULE_KEY, "[]")
        val type = object : TypeToken<List<ScheduleData>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun getScheduleForDate(date: Long): List<ScheduleData> {
        val calendar = Calendar.getInstance().apply { timeInMillis = date }
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        val startOfDay = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis

        return getAllSchedules().filter { schedule ->
            schedule.date in startOfDay until endOfDay
        }
    }

    fun deleteSchedule(scheduleId: String) {
        val scheduleList = getAllSchedules().toMutableList()
        scheduleList.removeAll { it.id == scheduleId }
        val json = gson.toJson(scheduleList)
        prefs.edit().putString(SCHEDULE_KEY, json).apply()
    }
}
