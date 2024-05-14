package com.example.storyapps.data

import androidx.paging.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.storyapps.api.ApiService
import com.example.storyapps.database.StoryDatabase
import com.example.storyapps.data.paging.StoryRemoteMediator
import com.example.storyapps.response.ListStoryItem
import com.example.storyapps.response.LoginResponse
import com.example.storyapps.response.RegisterResponse
import com.example.storyapps.response.StoryResponse
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {

    private var mockApi: ApiService = FakeApiService()
    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runBlocking {
        val remoteMediator = StoryRemoteMediator(
            mockDb,
            mockApi,
        )
        val pagingState = PagingState<Int, ListStoryItem>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}

class FakeApiService : ApiService {
    override suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return RegisterResponse(error = false, message = "")
    }

    override suspend fun login(email: String, password: String): LoginResponse {
        return LoginResponse()
    }

    override suspend fun addStory(
        image: MultipartBody.Part,
        description: RequestBody
    ): StoryResponse {
        return StoryResponse()
    }

    override suspend fun addStoryWithLocation(
        image: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody,
        lon: RequestBody
    ): StoryResponse {
        return StoryResponse()
    }

    override suspend fun getStoriesWithLocation(): StoryResponse {
        return StoryResponse()
    }

    override suspend fun getStories(page: Int, size: Int): StoryResponse {
        val items: MutableList<ListStoryItem> = arrayListOf()

        for (i in 0 until size) {
            val story = ListStoryItem(
                "https://bit.ly/4acjI2F",
                "2024-05-08T12:00:00",
                "Story ${page * size + i}",
                "Description for Story ${page * size + i}",
                0.0,
                (page * size + i).toString(),
                0.0
            )
            items.add(story)
        }

        return StoryResponse(listStory = items)
    }
}