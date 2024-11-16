package com.example.mob_dev_course

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mob_dev_course.fragments.Profile

class TopMenuActivity : AppCompatActivity() {

    private lateinit var backArrow: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topmenu)

        // Инициализация стрелки "назад"
        backArrow = findViewById(R.id.profile_icon)

        // Обработчик нажатия на стрелку назад
        backArrow.setOnClickListener {
            onBackPressed() // Стандартное поведение кнопки "Назад"
        }

        // Установка начального фрагмента (Profile)
        if (savedInstanceState == null) {
            openFragment(Profile())
        }
    }

    // Метод для замены фрагментов
    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_container, fragment)
            .addToBackStack(null) // Добавление в стек возврата
            .commit()
    }
}
