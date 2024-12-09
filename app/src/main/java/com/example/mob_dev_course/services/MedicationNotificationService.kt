package com.example.mob_dev_course.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.mob_dev_course.MainActivity
import com.example.mob_dev_course.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MedicationNotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Создаем канал уведомлений для Android 8.0 и выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Напоминания о лекарствах",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления о приеме лекарств"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent для открытия приложения при нажатии на уведомление
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Создаем уведомление
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(message.data["title"] ?: "Время принять лекарство")
            .setContentText(message.data["body"] ?: "Не забудьте принять лекарство вовремя")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        const val CHANNEL_ID = "medication_reminders"
    }
}
