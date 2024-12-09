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

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)

        auth = Firebase.auth

        val registerButton = findViewById<Button>(R.id.registerButton)
        val emailInput = findViewById<EditText>(R.id.emailField)
        val passwordInput = findViewById<EditText>(R.id.passwordField)
        val confirmPasswordInput = findViewById<EditText>(R.id.confirmPasswordField)

        registerButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Пароль должен содержать минимум 6 символов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Показываем прогресс
            registerButton.isEnabled = false

            // Регистрация через Firebase
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Успешная регистрация
                        Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        // Ошибка регистрации
                        Toast.makeText(this, "Ошибка регистрации: ${task.exception?.localizedMessage}",
                            Toast.LENGTH_SHORT).show()
                        registerButton.isEnabled = true
                    }
                }
        }

        // Добавляем кнопку возврата на экран входа
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            finish()
        }
    }
}