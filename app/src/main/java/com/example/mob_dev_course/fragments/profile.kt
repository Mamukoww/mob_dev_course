package com.example.mob_dev_course.fragments

import android.app.Activity
import android.content.Intent
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.mob_dev_course.LoginActivity
import com.example.mob_dev_course.R
import com.example.mob_dev_course.data.ProfileData
import com.example.mob_dev_course.data.ProfileStorage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class Profile : Fragment() {
    private lateinit var maleButton: Button
    private lateinit var femaleButton: Button
    private lateinit var saveButton: Button
    private lateinit var changePhotoButton: Button
    private lateinit var birthDateInput: EditText
    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var profileImage: ImageView
    private lateinit var profileStorage: ProfileStorage
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private var selectedDate: Calendar? = null
    private var selectedImageUri: Uri? = null

    private lateinit var auth: FirebaseAuth

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                saveImageLocally(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.profile_settings, container, false)
        
        auth = Firebase.auth

        profileImage = view.findViewById(R.id.profileImage)
        maleButton = view.findViewById(R.id.male_button)
        femaleButton = view.findViewById(R.id.female_button)
        saveButton = view.findViewById(R.id.save_button)
        changePhotoButton = view.findViewById(R.id.change_photo_button)
        birthDateInput = view.findViewById(R.id.birth_date)
        firstNameInput = view.findViewById(R.id.first_name)
        lastNameInput = view.findViewById(R.id.last_name)

        profileStorage = ProfileStorage(requireContext())

        val emailText = view.findViewById<TextView>(R.id.emailText)
        emailText.text = auth.currentUser?.email ?: "Не авторизован"

        view.findViewById<Button>(R.id.logoutButton).setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        setupGenderButtons()
        setupSaveButton()
        setupChangePhotoButton()
        setupBirthDatePicker()
        loadProfileData()
        loadProfileImage()

        return view
    }

    private fun setupChangePhotoButton() {
        changePhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getContent.launch(intent)
        }
    }

    private fun saveImageLocally(imageUri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            
            // Сохраняем изображение во внутреннем хранилище
            val fileName = "profile_${auth.currentUser?.uid}.jpg"
            val file = File(requireContext().filesDir, fileName)
            
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            
            // Обновляем отображение
            updateProfileImage(file)
            Toast.makeText(context, "Фото профиля обновлено", Toast.LENGTH_SHORT).show()
            
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка сохранения фото", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProfileImage() {
        val fileName = "profile_${auth.currentUser?.uid}.jpg"
        val file = File(requireContext().filesDir, fileName)
        
        if (file.exists()) {
            updateProfileImage(file)
        } else {
            // Используем изображение по умолчанию
            profileImage.setImageResource(R.drawable.user_avatar)
        }
    }

    private fun updateProfileImage(file: File) {
        context?.let { ctx ->
            Glide.with(ctx)
                .load(file)
                .circleCrop()
                .placeholder(R.drawable.user_avatar)
                .error(R.drawable.user_avatar)
                .into(profileImage)
        }
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
                saveProfile()
            }
        }
    }

    private fun saveProfile() {
        val profileData = ProfileData(
            firstName = firstNameInput.text.toString(),
            lastName = lastNameInput.text.toString(),
            gender = if (maleButton.isSelected) "male" else "female",
            birthDate = selectedDate?.timeInMillis
        )

        profileStorage.saveProfile(profileData)
        Toast.makeText(context, "Профиль сохранен", Toast.LENGTH_SHORT).show()

        // Возвращаемся в MainActivity для обновления UI
        activity?.let { activity ->
            activity.finish()
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

    private fun loadProfileData() {
        val profile = profileStorage.getProfile()

        firstNameInput.setText(profile.firstName)
        lastNameInput.setText(profile.lastName)

        when (profile.gender) {
            "male" -> {
                maleButton.isSelected = true
                femaleButton.isSelected = false
            }
            "female" -> {
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
