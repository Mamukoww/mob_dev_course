package com.example.mob_dev_course

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)

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

            // TODO: Добавить логику регистрации
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
