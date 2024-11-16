package com.example.mob_dev_course

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.mob_dev_course.R
import com.example.mob_dev_course.fragments.Profile

class TopMenuActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topmenu)

        // Инициализация кнопки "Назад"
        backButton = findViewById(R.id.back_button)

        // Установка слушателя нажатия на кнопку "Назад"
        backButton.setOnClickListener {
            onBackPressed() // Возвращаемся на предыдущую активность
        }

        // Всегда открывать фрагмент Profile при запуске
        replaceFragment(Profile())
    }

    private fun replaceFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_container, fragment) // Контейнер из activity_topmenu.xml
            .commit()
    }
}
