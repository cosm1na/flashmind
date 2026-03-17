package com.example.flashmind.network

data class LoginResponse(
    val succes: Boolean,
    val mesaj: String,
    val userId: Int? = null
)