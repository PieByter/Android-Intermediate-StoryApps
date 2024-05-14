package com.example.storyapps.viewmodel.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapps.data.repository.StoryRepository
import com.example.storyapps.data.datastore.UserPreference
import com.example.storyapps.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(
    private val storyRepository: StoryRepository,
    private val userPreference: UserPreference
) : ViewModel() {

    private val _storyList = MutableLiveData<List<ListStoryItem>>()
    val storyList: LiveData<List<ListStoryItem>> = _storyList

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun loadStories() {
        viewModelScope.launch {
            try {
                val storyResponse = storyRepository.getStories()
                val storyList = storyResponse.listStory
                if (storyList.isEmpty()) {
                    _errorMessage.value = "No stories found."
                } else {
                    _storyList.value = storyList
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch stories. Please try again."
                Log.e("MainViewModel", "Error fetching stories: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreference.clearToken()
            _errorMessage.value = "Logout successful"
        }
    }
}
