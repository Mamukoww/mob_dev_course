package com.example.mob_dev_course

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mob_dev_course.fragments.Profile
import com.example.mob_dev_course.fragments.NotificationsFragment

class TopMenuActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var topMenuTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topmenu)

        // Инициализация кнопки "Назад" и заголовка
        backButton = findViewById(R.id.back_button)
        topMenuTitle = findViewById(R.id.top_menu_title)

        // Установка слушателя нажатия на кнопку "Назад"
        backButton.setOnClickListener {
            onBackPressed() // Возвращаемся на предыдущую активность
        }

        // Получаем информацию о том, какой фрагмент нужно открыть
        val fragmentToOpen = intent.getStringExtra("fragment")

        // Меняем заголовок в зависимости от переданного флага
        if (fragmentToOpen == "notifications") {
            topMenuTitle.text = "Уведомления" // Меняем текст заголовка на "Уведомления"
            replaceFragment(NotificationsFragment()) // Открыть фрагмент уведомлений
        } else {
            topMenuTitle.text = "Профиль" // Меняем текст заголовка на "Профиль"
            replaceFragment(Profile()) // Открыть фрагмент профиля по умолчанию
        }
    }

    private fun replaceFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_container, fragment) // Контейнер для фрагментов в activity_topmenu.xml
            .commit()
    }
}
