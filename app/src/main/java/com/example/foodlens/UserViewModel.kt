package com.example.foodlens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    var hasShownGreeting by mutableStateOf(false)
        private set

    // Function to set the greeting as shown
    fun markGreetingAsShown() {
        hasShownGreeting = true
    }

    private val _category = MutableStateFlow<String?>(null)
    val category: StateFlow<String?> = _category

    fun setCategory(category: String) {
        _category.value = category
    }

}
