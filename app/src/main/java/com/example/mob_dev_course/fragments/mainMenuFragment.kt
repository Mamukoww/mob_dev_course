package com.example.mob_dev_course.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mob_dev_course.R

class MainMenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Подключение XML разметки фрагмента
        val view = inflater.inflate(R.layout.main_menu, container, false)

        // Привязка кнопок
        val button1: Button = view.findViewById(R.id.menu_button_1)
        val button2: Button = view.findViewById(R.id.menu_button_2)
        val button3: Button = view.findViewById(R.id.menu_button_3)
        val button4: Button = view.findViewById(R.id.menu_button_4)
        val button5: Button = view.findViewById(R.id.menu_button_5)
        val button6: Button = view.findViewById(R.id.menu_button_6)

        // Установка обработчиков кликов
        button1.setOnClickListener { showToast("Кнопка 1 нажата") }
        button2.setOnClickListener { showToast("Кнопка 2 нажата") }
        button3.setOnClickListener { showToast("Кнопка 3 нажата") }
        button4.setOnClickListener { showToast("Кнопка 4 нажата") }
        button5.setOnClickListener { showToast("Кнопка 5 нажата") }
        button6.setOnClickListener { showToast("Кнопка 6 нажата") }

        return view
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
