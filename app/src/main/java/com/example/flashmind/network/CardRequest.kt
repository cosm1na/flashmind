package com.example.flashmind.network

data class CardRequest(
    val id: Int? = null,
    val deckId: Int,
    val fata: String,
    val spate: String,

    val cutie: Int = 1,
    val dataViitoare: Long = System.currentTimeMillis()
)