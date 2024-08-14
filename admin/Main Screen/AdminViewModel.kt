package com.example.eventts.admin.main_screen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.eventts.dataClass.UserPreferences

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = UserPreferences(application)

    fun updateUsernameInSharedPreferences(username: String) {
        sharedPreferences.updateUsername(username)
    }

    fun updateProfileImageUrlInSharedPreferences(newProfileImageUrl: String?) {
        sharedPreferences.updateProfileImageUrl(newProfileImageUrl)
    }

    fun updateHeaderInSharedPreferences(newHeaderImageUrl: String?) {
        sharedPreferences.updateHeaderImageUrl(newHeaderImageUrl)
    }

}
