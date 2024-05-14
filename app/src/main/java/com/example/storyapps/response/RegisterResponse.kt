package com.example.storyapps.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String
)
