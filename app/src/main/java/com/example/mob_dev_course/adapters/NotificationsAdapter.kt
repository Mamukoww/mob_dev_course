package com.example.mob_dev_course.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mob_dev_course.R
import com.example.mob_dev_course.models.NotificationItem

class NotificationsAdapter : RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {
    private var notifications = mutableListOf<NotificationItem>()

    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.notificationTitle)
        val text: TextView = view.findViewById(R.id.notificationText)
        val time: TextView = view.findViewById(R.id.notificationTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_item, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.title.text = notification.title
        holder.text.text = notification.text
        holder.time.text = notification.time
    }

    override fun getItemCount() = notifications.size

    fun addNotification(notification: NotificationItem) {
        notifications.add(0, notification)
        notifyItemInserted(0)
    }

    fun clearNotifications() {
        val size = notifications.size
        notifications.clear()
        notifyItemRangeRemoved(0, size)
    }
}
