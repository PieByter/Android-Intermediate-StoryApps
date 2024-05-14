package com.example.storyapps.viewmodel.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapps.data.datastore.UserPreference
import com.example.storyapps.data.repository.StoryRepository

class MainViewModelFactory(
    private val storyRepository: StoryRepository,
    private val userPreference: UserPreference
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(storyRepository, userPreference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
