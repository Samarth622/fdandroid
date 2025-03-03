package com.example.foodlens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UserViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val userDao = AppDatabase.getDatabase(context).userDao()
        val repository = UserRepository(userDao)
        return UserViewModel(repository) as T
    }
}
