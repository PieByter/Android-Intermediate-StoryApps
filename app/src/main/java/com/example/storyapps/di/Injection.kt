package com.example.storyapps.di

import android.content.Context
import com.example.storyapps.data.repository.StoryRepository
import com.example.storyapps.data.datastore.UserPreference
import com.example.storyapps.data.datastore.dataStore
import com.example.storyapps.api.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getUser().first() }
        val apiService = ApiConfig.getApiService(user?.token)
        return StoryRepository.getInstance(apiService, pref)
    }
}