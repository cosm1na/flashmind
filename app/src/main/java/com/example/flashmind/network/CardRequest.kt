package com.example.flashmind.network

data class CardRequest(
    val id: Int? = null,
    val deckId: Int,
    val fata: String,
    val spate: String
)