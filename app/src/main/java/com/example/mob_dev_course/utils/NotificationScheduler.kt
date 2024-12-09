package com.example.mob_dev_course.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.mob_dev_course.receivers.AlarmReceiver
import java.util.concurrent.TimeUnit
import java.util.Calendar

object NotificationScheduler {
    
    fun scheduleMedicationReminder(
        context: Context,
        medicationId: String,
        medicationName: String,
        timeInMillis: Long,
        frequency: String,
        timesPerDay: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("medicationId", medicationId)
            putExtra("medicationName", medicationName)
        }

        // Создаем уникальный requestCode для каждого уведомления
        val requestCode = medicationId.hashCode() + timeInMillis.toInt()
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Устанавливаем время уведомления
        val calendar = Calendar.getInstance().apply {
            setTimeInMillis(timeInMillis)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        when (frequency.lowercase()) {
            "ежедневно" -> {
                // Устанавливаем ежедневное повторение
                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent),
                    pendingIntent
                )
            }
            "через день" -> {
                // Устанавливаем повторение через день
                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent),
                    pendingIntent
                )
                // Следующее уведомление через день
                calendar.add(Calendar.DAY_OF_MONTH, 2)
            }
            else -> {
                // Для других случаев устанавливаем одноразовое уведомление
                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent),
                    pendingIntent
                )
            }
        }
    }

    fun cancelMedicationReminder(context: Context, medicationId: String, timeInMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val requestCode = medicationId.hashCode() + timeInMillis.toInt()
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
        
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }
}
