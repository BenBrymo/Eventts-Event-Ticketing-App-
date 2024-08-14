package com.example.eventts.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class  UserViewModel: ViewModel() {

    private val _userName = MutableStateFlow("Username")
    val userName: StateFlow<String> = _userName

    init {
        fetchUserName()
    }

    private fun fetchUserName() {
        viewModelScope.launch {
            FirebaseFirestore.getInstance().collection("users")
                .document("adminId") // Replace with actual admin document ID
                .get()
                .addOnSuccessListener { document ->
                    _userName.value = document.getString("name") ?: "Admin Name"
                }
        }
    }

    fun logOut(onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        try {
            FirebaseAuth.getInstance().signOut()
            onSuccess()
        } catch (e: Exception) {
            onError(e)
        }
    }

}