package com.example.storyapps.data.repository

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapps.api.ApiService
import com.example.storyapps.data.paging.StoryPagingSource
import com.example.storyapps.data.paging.StoryRemoteMediator
import com.example.storyapps.database.StoryDatabase
import com.example.storyapps.response.ListStoryItem
import com.example.storyapps.response.StoryResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase
) {
    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun getApiService(): ApiService {
        return apiService
    }

    suspend fun addStory(description: String, imageFile: File, location: Location? = null) {
        val descriptionBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val imagePart = MultipartBody.Part.createFormData("photo", imageFile.name, requestImageFile)

        if (location != null) {
            val latBody = location.latitude.toString().toRequestBody("text/plain".toMediaType())
            val lonBody = location.longitude.toString().toRequestBody("text/plain".toMediaType())
            apiService.addStoryWithLocation(imagePart, descriptionBody, latBody, lonBody)
        } else {
            apiService.addStory(imagePart, descriptionBody)
        }
    }

    suspend fun getStoriesWithLocation(): StoryResponse {
        return apiService.getStoriesWithLocation()
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            apiService: ApiService,
            storyDatabase: StoryDatabase
        ): StoryRepository {
            return instance ?: synchronized(this) {
                instance ?: StoryRepository(
                    apiService,
                    storyDatabase
                ).also { instance = it }
            }
        }
    }
}
