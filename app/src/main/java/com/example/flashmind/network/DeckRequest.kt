package com.example.flashmind.network

data class DeckRequest(
    val id: Int? = null,
    val userId: Int,
    val titlu: String
)