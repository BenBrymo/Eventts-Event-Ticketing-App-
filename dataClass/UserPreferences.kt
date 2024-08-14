package com.example.eventts.dataClass

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel

class UserPreferences(application: Application): AndroidViewModel(application) {

    //creates a private sharedPreference
    private val sharedPreferences = getApplication<Application>().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    fun saveUserInfo(email: String, userName: String, profilePictureUrl: String?, headerPictureUrl: String?, role: String) {
        with(sharedPreferences.edit()) {
            putString("USER_EMAIL",email)
            putString("USERNAME",userName)
            putString("PROFILE_PICTURE_URL",profilePictureUrl)
            putString("HEADER_PICTURE_URL", headerPictureUrl)
            putString("ROLE",role)
            apply() // start saving process
        }
    }

    fun getUserInfo(): Map<String, String?> {
        //returns a map of info linked to shared preferences information
        return mapOf(
            "USER_EMAIL" to sharedPreferences.getString("USER_EMAIL",null),
            "USERNAME" to sharedPreferences.getString("USERNAME",null),
            "PROFILE_PICTURE_URL" to sharedPreferences.getString("PROFILE_PICTURE_URL",null),
            "HEADER_PICTURE_URL" to sharedPreferences.getString("HEADER_PICTURE_URL",null),
            "ROLE" to sharedPreferences.getString("ROLE",null)
        )
    }


    fun updateUsername(newUsername: String) {
        with(sharedPreferences.edit()) {
            //update name
            putString("USERNAME",newUsername)
                .apply() //apply edits
        }
    }

    fun updateProfileImageUrl(newProfileImageUrl: String?) {
        try {
            with(sharedPreferences.edit()) {
                putString("PROFILE_PICTURE_URL", newProfileImageUrl)
                apply() // Save changes asynchronously
            }
        } catch (e: Exception) {
            Log.e("UserPreferences", "Failed to update profile image URL", e)
        }
    }

    fun updateHeaderImageUrl(newHeaderImageUrl: String?) {
        try {
            with(sharedPreferences.edit()) {
                putString("HEADER_PICTURE_URL", newHeaderImageUrl)
                apply() // Save changes asynchronously
            }
        } catch (e: Exception) {
            Log.e("UserPreferences", "Failed to update header image URL", e)
        }
    }
}