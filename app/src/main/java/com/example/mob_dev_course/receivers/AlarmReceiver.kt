package com.example.mob_dev_course.receivers

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.mob_dev_course.MainActivity
import com.example.mob_dev_course.R
import com.example.mob_dev_course.fragments.NotificationsFragment
import com.example.mob_dev_course.services.MedicationNotificationService
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        val medicationId = intent.getStringExtra("medicationId") ?: return
        val medicationName = intent.getStringExtra("medicationName") ?: "лекарство"
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8.0 и выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MedicationNotificationService.CHANNEL_ID,
                "Напоминания о лекарствах",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления о приеме лекарств"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent для открытия приложения при нажатии на уведомление
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            medicationId.hashCode(),
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Создаем уведомление
        val notification = NotificationCompat.Builder(context, MedicationNotificationService.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Время принять лекарство")
            .setContentText("Не забудьте принять $medicationName")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .build()

        // Показываем уведомление
        notificationManager.notify(medicationId.hashCode(), notification)

        // Добавляем уведомление в NotificationsFragment
        NotificationsFragment.addNotification(
            title = "Время принять лекарство",
            text = "Не забудьте принять $medicationName",
            medicationId = medicationId
        )

        // Если это ежедневное уведомление, планируем следующее на завтра
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val nextAlarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("medicationId", medicationId)
            putExtra("medicationName", medicationName)
        }
        val nextPendingIntent = PendingIntent.getBroadcast(
            context,
            medicationId.hashCode(),
            nextAlarmIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val nextAlarmTime = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(nextAlarmTime.timeInMillis, nextPendingIntent),
            nextPendingIntent
        )
    }
}
