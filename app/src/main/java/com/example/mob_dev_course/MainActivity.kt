package com.example.mob_dev_course

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mob_dev_course.fragments.MainMenuFragment
import com.example.mob_dev_course.fragments.PlannedFragment
import com.example.mob_dev_course.fragments.Fragment3

class MainActivity : AppCompatActivity() {

    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var profileButton: Button
    private lateinit var notificationButton: Button // Кнопка уведомлений

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация кнопок
        button1 = findViewById(R.id.bottom_button_1)
        button2 = findViewById(R.id.bottom_button_2)
        button3 = findViewById(R.id.bottom_button_3)
        profileButton = findViewById(R.id.profile_button)
        notificationButton = findViewById(R.id.notification_button) // Инициализация кнопки уведомлений

        // Установка начального фрагмента
        if (savedInstanceState == null) {
            replaceFragment(MainMenuFragment())
        }

        // Обработчики нажатий
        button1.setOnClickListener { replaceFragment(MainMenuFragment()) }
        button2.setOnClickListener { replaceFragment(PlannedFragment()) }
        button3.setOnClickListener { replaceFragment(Fragment3()) }

        // Переход на TopMenuActivity для профиля
        profileButton.setOnClickListener {
            val intent = Intent(this, TopMenuActivity::class.java)
            intent.putExtra("fragment", "profile") // Отправка флага, чтобы открыть фрагмент Profile
            startActivity(intent)
        }

        // Переход на TopMenuActivity для уведомлений
        notificationButton.setOnClickListener {
            val intent = Intent(this, TopMenuActivity::class.java)
            intent.putExtra("fragment", "notifications") // Отправка флага, чтобы открыть фрагмент Notifications
            startActivity(intent)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_container, fragment)
            .addToBackStack(null) // Сохранение состояния в стеке назад
            .commit()
    }
}
