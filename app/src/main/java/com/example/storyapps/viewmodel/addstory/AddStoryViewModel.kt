package com.example.storyapps.viewmodel.addstory

import androidx.lifecycle.ViewModel
import com.example.storyapps.data.repository.StoryRepository
import java.io.File

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    suspend fun uploadImage(file: File, description: String) =
        storyRepository.addStory(description, file)
}