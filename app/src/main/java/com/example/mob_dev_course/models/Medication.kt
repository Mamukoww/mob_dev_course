package com.example.mob_dev_course.models

data class Medication(
    val name: String,
    val dosage: String,
    val frequency: String,
    val time: String,
    val startDate: String,
    val endDate: String,
    val notes: String = ""
)
