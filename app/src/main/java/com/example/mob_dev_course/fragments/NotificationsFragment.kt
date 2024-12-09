package com.example.mob_dev_course.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mob_dev_course.R
import com.example.mob_dev_course.adapters.NotificationsAdapter
import com.example.mob_dev_course.models.NotificationItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var clearButton: Button
    private lateinit var adapter: NotificationsAdapter

    companion object {
        private val notificationsAdapter = NotificationsAdapter()
        private var notificationCountListener: ((Int) -> Unit)? = null
        
        fun addNotification(title: String, text: String, medicationId: String) {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val currentTime = timeFormat.format(Date())
            
            val notification = NotificationItem(
                title = title,
                text = text,
                time = currentTime,
                medicationId = medicationId
            )
            notificationsAdapter.addNotification(notification)
            notificationCountListener?.invoke(notificationsAdapter.itemCount)
        }

        fun setNotificationCountListener(listener: (Int) -> Unit) {
            notificationCountListener = listener
            listener.invoke(notificationsAdapter.itemCount)
        }

        fun getNotificationCount(): Int {
            return notificationsAdapter.itemCount
        }

        fun clearAllNotifications() {
            notificationsAdapter.clearNotifications()
            notificationCountListener?.invoke(0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        recyclerView = view.findViewById(R.id.notificationsRecyclerView)
        emptyText = view.findViewById(R.id.emptyNotificationsText)
        clearButton = view.findViewById(R.id.clearNotificationsButton)
        
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = notificationsAdapter
        recyclerView.adapter = adapter

        updateEmptyState()

        clearButton.setOnClickListener {
            clearAllNotifications()
            updateEmptyState()
        }
    }

    private fun updateEmptyState() {
        if (adapter.itemCount == 0) {
            emptyText.visibility = View.VISIBLE
            clearButton.visibility = View.GONE
        } else {
            emptyText.visibility = View.GONE
            clearButton.visibility = View.VISIBLE
        }
    }
}
