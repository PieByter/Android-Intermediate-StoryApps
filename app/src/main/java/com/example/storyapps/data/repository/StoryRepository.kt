package com.example.storyapps.data.repository

import com.example.storyapps.data.datastore.UserPreference
import com.example.storyapps.response.StoryResponse
import com.example.storyapps.api.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun getStories(): StoryResponse {
        return apiService.getStories()
    }

    fun getApiService(): ApiService {
        return apiService
    }

    suspend fun addStory(description: String, imageFile: File) {
        val descriptionBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val imagePart = MultipartBody.Part.createFormData("photo", imageFile.name, requestImageFile)

        apiService.addStory(imagePart, descriptionBody)
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference): StoryRepository {
            return instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference).also { instance = it }
            }
        }
    }
}
