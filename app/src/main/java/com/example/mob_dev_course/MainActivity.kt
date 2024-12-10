package com.example.mob_dev_course

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mob_dev_course.fragments.MainMenuFragment
import com.example.mob_dev_course.fragments.PlannedFragment
import com.example.mob_dev_course.fragments.Fragment3
import com.example.mob_dev_course.fragments.NotificationsFragment
import com.example.mob_dev_course.data.ProfileStorage
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var profileButton: ImageView
    private lateinit var notificationsButton: Button
    private lateinit var addButton: Button
    private lateinit var notificationCount: TextView
    private lateinit var usernameText: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var profileStorage: ProfileStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация Firebase Auth
        auth = Firebase.auth
        profileStorage = ProfileStorage(this)

        // Проверяем авторизацию
        if (auth.currentUser == null) {
            // Если пользователь не авторизован, перенаправляем на экран приветствия
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        // Инициализация views
        button1 = findViewById(R.id.bottom_button_1)
        button2 = findViewById(R.id.bottom_button_2)
        button3 = findViewById(R.id.bottom_button_3)
        profileButton = findViewById(R.id.profile_button)
        notificationsButton = findViewById(R.id.notification_button)
        addButton = findViewById(R.id.add_button)
        notificationCount = findViewById(R.id.notificationCount)
        usernameText = findViewById(R.id.username_text)

        if (savedInstanceState == null) {
            replaceFragment(MainMenuFragment())
        }

        setupButtons()
        updateProfileInfo()
    }

    private fun setupButtons() {
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

        button1.setOnClickListener { replaceFragment(MainMenuFragment()) }
        button2.setOnClickListener { replaceFragment(PlannedFragment()) }
        button3.setOnClickListener { replaceFragment(Fragment3()) }

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
    }

    override fun onResume() {
        super.onResume()
        updateProfileInfo()
    }

    private fun updateProfileInfo() {
        // Обновляем имя пользователя
        val profileData = profileStorage.getProfile()
        val displayName = if (!profileData.firstName.isEmpty()) {
            if (!profileData.lastName.isEmpty()) {
                "${profileData.firstName} ${profileData.lastName}"
            } else {
                profileData.firstName
            }
        } else {
            auth.currentUser?.email ?: "Пользователь"
        }
        usernameText.text = displayName

        // Обновляем фото профиля
        val fileName = "profile_${auth.currentUser?.uid}.jpg"
        val file = File(filesDir, fileName)
        
        if (file.exists()) {
            Glide.with(this)
                .load(file)
                .circleCrop()
                .placeholder(R.drawable.user_avatar)
                .error(R.drawable.user_avatar)
                .into(profileButton)
        } else {
            profileButton.setImageResource(R.drawable.user_avatar)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_container, fragment)
            .commit()
    }
}