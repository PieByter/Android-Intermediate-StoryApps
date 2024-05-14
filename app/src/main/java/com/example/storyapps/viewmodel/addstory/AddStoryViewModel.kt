package com.example.storyapps.viewmodel.addstory

import android.location.Location
import androidx.lifecycle.ViewModel
import com.example.storyapps.data.repository.StoryRepository
import java.io.File

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    suspend fun uploadImage(file: File, description: String, location: Location? = null) {
        storyRepository.addStory(description, file, location)
    }
}
