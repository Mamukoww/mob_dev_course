package com.example.mob_dev_course

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mob_dev_course.fragments.MainMenuFragment
import com.example.mob_dev_course.fragments.PlannedFragment
import com.example.mob_dev_course.fragments.Fragment3
import com.example.mob_dev_course.fragments.NotificationsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var button1: ImageView
    private lateinit var button2: ImageView
    private lateinit var button3: ImageView
    private lateinit var profileButton: ImageView
    private lateinit var notificationsButton: ImageView
    private lateinit var addButton: ImageView
    private lateinit var notificationCount: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация Firebase Auth
        auth = Firebase.auth

        // Проверяем авторизацию
        if (auth.currentUser == null) {
            // Если пользователь не авторизован, перенаправляем на экран приветствия
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        try {
            button1 = findViewById(R.id.bottom_button_1)
            button2 = findViewById(R.id.bottom_button_2)
            button3 = findViewById(R.id.bottom_button_3)
            profileButton = findViewById(R.id.profile_button)
            notificationsButton = findViewById(R.id.notification_button)
            addButton = findViewById(R.id.add_button)
            notificationCount = findViewById(R.id.notificationCount)

            if (savedInstanceState == null) {
                replaceFragment(MainMenuFragment())
            }

            button1.setOnClickListener { replaceFragment(MainMenuFragment()) }
            button2.setOnClickListener { replaceFragment(PlannedFragment()) }
            button3.setOnClickListener { replaceFragment(Fragment3()) }

            profileButton.setOnClickListener {
                val intent = Intent(this, TopMenuActivity::class.java)
                intent.putExtra("fragment", "profile")
                startActivity(intent)
            }

            notificationsButton.setOnClickListener {
                val intent = Intent(this, TopMenuActivity::class.java)
                intent.putExtra("fragment", "notifications")
                startActivity(intent)
            }

            addButton.setOnClickListener {
                val intent = Intent(this, TopMenuActivity::class.java)
                intent.putExtra("fragment", "drug_settings")
                startActivity(intent)
            }

            // Устанавливаем слушатель количества уведомлений
            NotificationsFragment.setNotificationCountListener { count ->
                runOnUiThread {
                    if (count > 0) {
                        notificationCount.visibility = View.VISIBLE
                        notificationCount.text = count.toString()
                    } else {
                        notificationCount.visibility = View.GONE
                    }
                }
            }

            // Инициализируем начальное количество уведомлений
            val initialCount = NotificationsFragment.getNotificationCount()
            if (initialCount > 0) {
                notificationCount.visibility = View.VISIBLE
                notificationCount.text = initialCount.toString()
            } else {
                notificationCount.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Показываем ошибку пользователю и возвращаемся на экран входа
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}