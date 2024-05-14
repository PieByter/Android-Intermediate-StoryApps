package com.example.storyapps.viewmodel.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapps.data.repository.Repository
import com.example.storyapps.response.LoginResponse

class LoginViewModel(private val repository: Repository) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    suspend fun login(email: String, password: String) {
        _isLoading.value = true
        try {
            val response = repository.login(email, password)
            _loginResult.value = response
        } catch (e: Exception) {
            _errorMessage.value = e.localizedMessage
        } finally {
            _isLoading.value = false
        }
    }
}
