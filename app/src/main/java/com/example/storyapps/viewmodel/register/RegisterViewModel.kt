package com.example.storyapps.viewmodel.register

import androidx.lifecycle.ViewModel
import com.example.storyapps.data.repository.Repository

class RegisterViewModel(private val repository: Repository) : ViewModel() {

    suspend fun register(name: String, email: String, password: String) =
        repository.register(name, email, password)
}