package com.example.storyapps.data.repository

import com.example.storyapps.data.datastore.UserPreference
import com.example.storyapps.response.LoginResponse
import com.example.storyapps.response.RegisterResponse
import com.example.storyapps.api.ApiConfig
import kotlinx.coroutines.flow.firstOrNull

class Repository(private val authDataStore: UserPreference) {

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        val token = authDataStore.getToken().firstOrNull()
        val apiService = ApiConfig.getApiService(token)
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        val token = authDataStore.getToken().firstOrNull()
        val apiService = ApiConfig.getApiService(token)
        return apiService.login(email, password)
    }
}


