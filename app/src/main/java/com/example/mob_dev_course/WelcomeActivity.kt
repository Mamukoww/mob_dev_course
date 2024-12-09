package com.example.mob_dev_course

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class WelcomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация Firebase Auth
        auth = Firebase.auth

        // Проверяем, авторизован ли пользователь
        if (auth.currentUser != null) {
            // Если пользователь авторизован, сразу переходим на главный экран
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        // Если пользователь не авторизован, показываем экран приветствия
        setContentView(R.layout.welcome)

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}
