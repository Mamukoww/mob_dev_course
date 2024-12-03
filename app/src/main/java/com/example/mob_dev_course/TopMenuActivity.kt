package com.example.mob_dev_course

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mob_dev_course.fragments.Profile
import com.example.mob_dev_course.fragments.NotificationsFragment
import com.example.mob_dev_course.fragments.DrugSettingTimeFragment
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

        val fragmentToOpen = intent.getStringExtra("fragment")

        when (fragmentToOpen) {
            "notifications" -> {
                topMenuTitle.text = "Уведомления"
                replaceFragment(NotificationsFragment())
            }
            "profile" -> {
                topMenuTitle.text = "Профиль"
                replaceFragment(Profile())
            }
            "detailed_plan" -> {
                topMenuTitle.text = "Подробнее"
                replaceFragment(DetailedPlan())
            }
            "drug_settings" -> {
                topMenuTitle.text = "Препарат"
                replaceFragment(DrugSettingTimeFragment())
            }
            else -> {
                topMenuTitle.text = "Профиль"
                replaceFragment(Profile())
            }
        }
    }

    private fun replaceFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_container, fragment)
            .commit()
    }
}
