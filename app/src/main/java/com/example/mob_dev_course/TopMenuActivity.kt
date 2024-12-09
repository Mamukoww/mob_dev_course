package com.example.mob_dev_course

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mob_dev_course.fragments.Profile
import com.example.mob_dev_course.fragments.NotificationsFragment
import com.example.mob_dev_course.fragments.DrugSettingsFragment
import com.example.mob_dev_course.fragments.DetailedPlan

class TopMenuActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var topMenuTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topmenu)

        backButton = findViewById(R.id.back_button)
        topMenuTitle = findViewById(R.id.top_menu_title)

        backButton.setOnClickListener {
            onBackPressed()
        }

        val fragmentType = intent.getStringExtra("fragment")
        
        // Устанавливаем заголовок в зависимости от типа фрагмента
        topMenuTitle.text = when (fragmentType) {
            "profile" -> "Профиль"
            "notifications" -> "Уведомления"
            "detailed_plan" -> "Лекарства"
            "drug_settings" -> "Добавление лекарства"
            else -> "Профиль"
        }
        
        val fragment = when (fragmentType) {
            "profile" -> Profile()
            "notifications" -> NotificationsFragment()
            "detailed_plan" -> DetailedPlan()
            "drug_settings" -> DrugSettingsFragment()
            else -> Profile()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.content_container, fragment)
            .commit()
    }

    private fun replaceFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_container, fragment)
            .commit()
    }
}