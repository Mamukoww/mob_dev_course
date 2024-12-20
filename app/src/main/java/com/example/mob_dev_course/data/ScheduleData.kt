package com.example.mob_dev_course.data

enum class MedicationStatus {
    PENDING,    // Ожидает приема (иконка лекарства)
    TAKEN,      // Принято (галочка)
    SKIPPED,    // Пропущено (стрелка)
    CANCELLED   // Отменено (крестик)
}

data class ScheduleData(
    val id: String = java.util.UUID.randomUUID().toString(),
    val medicationId: String, // ID лекарства
    val medicationName: String,
    val time: String,
    val description: String,
    val date: Long, // Дата в миллисекундах
    val frequency: String,
    val timesPerDay: Int,
    val startDate: Long? = null, // Дата начала периода в миллисекундах
    val endDate: Long? = null,   // Дата окончания периода в миллисекундах
    var status: MedicationStatus = MedicationStatus.PENDING // Статус приема
)
