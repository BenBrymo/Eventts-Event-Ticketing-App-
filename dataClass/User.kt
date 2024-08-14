package com.example.eventts.dataClass

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


private val _users = MutableStateFlow<List<User>>(emptyList())
val users: StateFlow<List<User>> = _users


data class User(
    val id: String = "",
    val userName: String = "",
    val email: String = "",
    val role: String = "",
    val isSuspended: Boolean = false,
    val profilePictureUrl: String? = null
) {
    fun addUser(id:String, userName: String, email: String,role: String,isSuspended: Boolean,profilePictureUrl: String?) {
        val newUser = User(id,userName,email,role,isSuspended,profilePictureUrl)
        //checks if user already exists
        if (_users.value.contains(newUser)) {
            //logs a message
            Log.d("AddUserFunction","User already exists")
        }else {
            //add to users list
            _users.value += newUser
            Log.d("AddUserFunction","User created")
        }
    }

}

