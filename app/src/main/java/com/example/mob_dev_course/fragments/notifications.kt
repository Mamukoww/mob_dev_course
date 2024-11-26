package com.example.mob_dev_course.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mob_dev_course.R

class NotificationsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Подключение макета для уведомлений
        return inflater.inflate(R.layout.notifications, container, false)
    }
}
