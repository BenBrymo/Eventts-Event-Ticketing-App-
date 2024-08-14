package com.example.eventts.common.PasswordReset

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

class PasswordRecoveryViewModel: ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun sendPasswordResetEmail(email: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                //sends recovery link to email account and waits for result
                firebaseAuth.sendPasswordResetEmail(email).await()
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}