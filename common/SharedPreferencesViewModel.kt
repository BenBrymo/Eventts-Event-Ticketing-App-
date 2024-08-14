package com.example.eventts.common

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventts.dataClass.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SharedPreferencesViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = UserPreferences(application)

    //MutableStateFlow to keep sharedPreference Map
    private val _userInfo = MutableStateFlow(sharedPreferences.getUserInfo())
    val userInfo: StateFlow<Map<String,String?>> = _userInfo


}