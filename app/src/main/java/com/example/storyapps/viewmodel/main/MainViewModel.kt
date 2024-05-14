package com.example.storyapps.viewmodel.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapps.data.repository.StoryRepository
import com.example.storyapps.data.datastore.UserPreference
import com.example.storyapps.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(
    storyRepository: StoryRepository,
    private val userPreference: UserPreference
) : ViewModel() {

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    val story: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStories().cachedIn(viewModelScope)

    fun logout() {
        viewModelScope.launch {
            userPreference.clearToken()
            _errorMessage.value = "Logout successful"
        }
    }
}
