package com.example.mob_dev_course

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mob_dev_course.fragments.MainMenuFragment
import com.example.mob_dev_course.fragments.Fragment2
import com.example.mob_dev_course.fragments.Fragment3

class MainActivity : AppCompatActivity() {

    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация кнопок
        button1 = findViewById(R.id.bottom_button_1)
        button2 = findViewById(R.id.bottom_button_2)
        button3 = findViewById(R.id.bottom_button_3)

        // Установка начального фрагмента
        if (savedInstanceState == null) {
            replaceFragment(MainMenuFragment())
        }

        // Обработчики нажатий
        button1.setOnClickListener { replaceFragment(MainMenuFragment()) }
        button2.setOnClickListener { replaceFragment(Fragment2()) }
        button3.setOnClickListener { replaceFragment(Fragment3()) }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_container, fragment)
            .commit()
    }
}
