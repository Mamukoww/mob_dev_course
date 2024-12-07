package com.example.mob_dev_course.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

data class TimeSchedule(
    val hour: Int,
    val minute: Int,
    val daysOfWeek: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7) // По умолчанию каждый день
)

data class Medication(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: String,
    val comment: String,
    val frequency: String,
    val timesPerDay: Int,
    val schedules: List<TimeSchedule>,
    val isActive: Boolean = true
)

class MedicationStorage(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("medications_prefs", Context.MODE_PRIVATE)
    private val gson: Gson = Gson()

    companion object {
        private const val KEY_MEDICATIONS = "medications"
    }

    fun saveMedication(medication: Medication) {
        val medications = getMedications().toMutableList()
        medications.add(medication)
        saveMedicationList(medications)
    }

    fun getMedications(): List<Medication> {
        val json = sharedPreferences.getString(KEY_MEDICATIONS, null)
        return if (json != null) {
            val type = object : TypeToken<List<Medication>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun getActiveMedications(): List<Medication> {
        return getMedications().filter { it.isActive }
    }

    fun getInactiveMedications(): List<Medication> {
        return getMedications().filter { !it.isActive }
    }

    fun getMedicationSchedulesForDay(dayOfWeek: Int): List<Pair<Medication, TimeSchedule>> {
        return getMedications()
            .filter { it.isActive }
            .flatMap { medication ->
                medication.schedules
                    .filter { schedule -> dayOfWeek in schedule.daysOfWeek }
                    .map { schedule -> medication to schedule }
            }
            .sortedWith(compareBy({ it.second.hour }, { it.second.minute }))
    }

    private fun saveMedicationList(medications: List<Medication>) {
        val json = gson.toJson(medications)
        sharedPreferences.edit().putString(KEY_MEDICATIONS, json).apply()
    }

    fun updateMedicationStatus(medicationId: String, isActive: Boolean) {
        val medications = getMedications().toMutableList()
        val index = medications.indexOfFirst { it.id == medicationId }
        if (index != -1) {
            val updatedMedication = medications[index].copy(isActive = isActive)
            medications[index] = updatedMedication
            saveMedicationList(medications)
        }
    }

    fun deleteMedication(medicationId: String) {
        val medications = getMedications().toMutableList()
        medications.removeAll { it.id == medicationId }
        saveMedicationList(medications)
    }

    fun getMedicationByName(name: String): Medication? {
        return getMedications().find { it.name == name }
    }
}
