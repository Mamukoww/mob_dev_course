package com.example.mob_dev_course

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome)

        // Находим кнопки
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)

        // Обработчик для кнопки входа
        loginButton.setOnClickListener {
            // Переход к экрану входа
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Обработчик для кнопки регистрации
        registerButton.setOnClickListener {
            // Переход к экрану регистрации
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}
