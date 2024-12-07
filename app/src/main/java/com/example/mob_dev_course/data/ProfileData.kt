package com.example.mob_dev_course.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.util.*

data class ProfileData(
    val firstName: String = "",
    val lastName: String = "",
    val gender: String = "",
    val birthDate: Long? = null
)

class ProfileStorage(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
    private val gson: Gson = Gson()

    companion object {
        private const val KEY_PROFILE = "profile_data"
    }

    fun saveProfile(profile: ProfileData) {
        val json = gson.toJson(profile)
        sharedPreferences.edit().putString(KEY_PROFILE, json).apply()
    }

    fun getProfile(): ProfileData {
        val json = sharedPreferences.getString(KEY_PROFILE, null)
        return if (json != null) {
            gson.fromJson(json, ProfileData::class.java)
        } else {
            ProfileData()
        }
    }
}
