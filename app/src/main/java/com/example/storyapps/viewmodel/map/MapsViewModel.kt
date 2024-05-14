package com.example.storyapps.viewmodel.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.storyapps.data.repository.StoryRepository
import com.example.storyapps.response.StoryResponse

class MapsViewModel(
    private val storyRepository: StoryRepository,
) : ViewModel() {
    fun getStoriesWithLocation(): LiveData<StoryResponse> {
        return liveData {
            emit(storyRepository.getStoriesWithLocation())
        }
    }
}