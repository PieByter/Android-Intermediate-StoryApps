package com.example.storyapps.api

import com.example.storyapps.response.LoginResponse
import com.example.storyapps.response.RegisterResponse
import com.example.storyapps.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(
    ): StoryResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Part image: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): StoryResponse

}
