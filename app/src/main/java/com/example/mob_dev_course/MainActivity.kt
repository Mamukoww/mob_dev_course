package com.example.mob_dev_course

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mob_dev_course.fragments.Fragment1
import com.example.mob_dev_course.fragments.Fragment2
import com.example.mob_dev_course.fragments.Fragment3
import com.example.mob_dev_course.fragments.Profile

class MainActivity : AppCompatActivity() {

    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var profileButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация кнопок
        button1 = findViewById(R.id.bottom_button_1)
        button2 = findViewById(R.id.bottom_button_2)
        button3 = findViewById(R.id.bottom_button_3)
        profileButton = findViewById(R.id.profile_button)

        // Установка начального фрагмента
        if (savedInstanceState == null) {
            replaceFragment(Fragment1())
        }

        // Обработчики нажатий
        button1.setOnClickListener { replaceFragment(Fragment1()) }
        button2.setOnClickListener { replaceFragment(Fragment2()) }
        button3.setOnClickListener { replaceFragment(Fragment3()) }

        // Переход на настройки профиля
        profileButton.setOnClickListener {
            replaceFragment(Profile())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_container, fragment)
            .addToBackStack(null) // Добавление в стек возврата
            .commit()
    }
}
