package com.example.mob_dev_course.models

import java.util.UUID

data class Medication(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: String,
    val comment: String = "",
    val frequency: String,
    val timesPerDay: Int,
    val schedules: List<TimeSchedule> = emptyList(),
    val startDate: String = "",
    val endDate: String = ""
)

data class TimeSchedule(
    val hour: Int,
    val minute: Int
) {
    override fun toString(): String = String.format("%02d:%02d", hour, minute)
}
