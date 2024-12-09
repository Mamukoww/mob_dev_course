package com.example.mob_dev_course

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in)

        auth = Firebase.auth

        // Проверяем, авторизован ли пользователь
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        val loginButton = findViewById<Button>(R.id.loginButton)
        val emailInput = findViewById<EditText>(R.id.usernameField)
        val passwordInput = findViewById<EditText>(R.id.passwordField)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Показываем прогресс
            loginButton.isEnabled = false

            // Выполняем вход через Firebase
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Успешный вход
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        // Ошибка входа
                        Toast.makeText(this, "Ошибка входа: ${task.exception?.localizedMessage}",
                            Toast.LENGTH_SHORT).show()
                        loginButton.isEnabled = true
                    }
                }
        }

        // Добавляем кнопку перехода на регистрацию
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}