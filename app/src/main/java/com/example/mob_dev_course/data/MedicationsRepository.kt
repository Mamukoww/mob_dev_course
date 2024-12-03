package com.example.mob_dev_course.data

import com.example.mob_dev_course.models.Medication

object MedicationsRepository {
    private val medications = mutableListOf<Medication>()

    fun addMedication(medication: Medication) {
        medications.add(medication)
    }

    fun getAllMedications(): List<Medication> = medications.toList()
}
