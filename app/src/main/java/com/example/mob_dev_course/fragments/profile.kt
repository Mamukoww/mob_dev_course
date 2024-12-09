package com.example.mob_dev_course.fragments

import android.content.Intent
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mob_dev_course.LoginActivity
import com.example.mob_dev_course.R
import com.example.mob_dev_course.data.ProfileData
import com.example.mob_dev_course.data.ProfileStorage
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Profile : Fragment() {
    private lateinit var maleButton: Button
    private lateinit var femaleButton: Button
    private lateinit var saveButton: Button
    private lateinit var changePhotoButton: Button
    private lateinit var birthDateInput: EditText
    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var profileStorage: ProfileStorage
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private var selectedDate: Calendar? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.profile_settings, container, false)
        auth = Firebase.auth

        // Отображаем email пользователя
        val emailText = view.findViewById<TextView>(R.id.emailText)
        emailText.text = auth.currentUser?.email ?: "Не авторизован"

        // Добавляем кнопку выхода
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            auth.signOut()
            // Перенаправляем на экран входа
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        return view
        val view = inflater.inflate(R.layout.profile_settings, container, false)

        maleButton = view.findViewById(R.id.male_button)
        femaleButton = view.findViewById(R.id.female_button)
        saveButton = view.findViewById(R.id.save_button)
        changePhotoButton = view.findViewById(R.id.change_photo_button)
        birthDateInput = view.findViewById(R.id.birth_date)
        firstNameInput = view.findViewById(R.id.first_name)
        lastNameInput = view.findViewById(R.id.last_name)

        profileStorage = ProfileStorage(requireContext())

        setupGenderButtons()
        setupSaveButton()
        setupChangePhotoButton()
        setupBirthDatePicker()
        loadProfileData()

        return view
    }

    private fun setupGenderButtons() {
        maleButton.setOnClickListener {
            maleButton.isSelected = true
            femaleButton.isSelected = false
        }

        femaleButton.setOnClickListener {
            femaleButton.isSelected = true
            maleButton.isSelected = false
        }
    }

    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            if (validateInputs()) {
                saveProfileData()
                Toast.makeText(context, "Профиль сохранен", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupChangePhotoButton() {
        changePhotoButton.setOnClickListener {
            Toast.makeText(context, "Функция изменения фото будет добавлена позже", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBirthDatePicker() {
        birthDateInput.isFocusable = false
        birthDateInput.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val calendar = selectedDate ?: Calendar.getInstance().apply {
            add(Calendar.YEAR, -18)
        }

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDate = calendar
                birthDateInput.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            val minDate = Calendar.getInstance().apply {
                add(Calendar.YEAR, -100)
            }
            val maxDate = Calendar.getInstance()
            datePicker.minDate = minDate.timeInMillis
            datePicker.maxDate = maxDate.timeInMillis
        }.show()
    }

    private fun validateInputs(): Boolean {
        if (firstNameInput.text.isNullOrEmpty()) {
            Toast.makeText(context, "Пожалуйста, введите имя", Toast.LENGTH_SHORT).show()
            return false
        }
        if (lastNameInput.text.isNullOrEmpty()) {
            Toast.makeText(context, "Пожалуйста, введите фамилию", Toast.LENGTH_SHORT).show()
            return false
        }
        if (birthDateInput.text.isNullOrEmpty()) {
            Toast.makeText(context, "Пожалуйста, укажите дату рождения", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun saveProfileData() {
        val profile = ProfileData(
            firstName = firstNameInput.text.toString(),
            lastName = lastNameInput.text.toString(),
            gender = if (maleButton.isSelected) "М" else if (femaleButton.isSelected) "Ж" else "",
            birthDate = selectedDate?.timeInMillis
        )
        profileStorage.saveProfile(profile)
    }

    private fun loadProfileData() {
        val profile = profileStorage.getProfile()

        firstNameInput.setText(profile.firstName)
        lastNameInput.setText(profile.lastName)

        when (profile.gender) {
            "М" -> {
                maleButton.isSelected = true
                femaleButton.isSelected = false
            }
            "Ж" -> {
                femaleButton.isSelected = true
                maleButton.isSelected = false
            }
        }

        profile.birthDate?.let { timestamp ->
            selectedDate = Calendar.getInstance().apply {
                timeInMillis = timestamp
            }
            birthDateInput.setText(dateFormat.format(selectedDate?.time))
        }
    }
}
