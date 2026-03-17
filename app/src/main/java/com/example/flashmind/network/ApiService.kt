package com.example.flashmind.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("/register")
    suspend fun registerUser(@Body request: AuthRequest): Response<ResponseBody>

    @POST("/login")
    suspend fun loginUser(@Body request: AuthRequest): Response<LoginResponse>

    @POST("/decks")
    suspend fun createDeck(@Body deck: DeckRequest): Response<DeckRequest>

    @GET("/decks/{userId}")
    suspend fun getDecks(@Path("userId") userId: Int): Response<List<DeckRequest>>

    @POST("/cards")
    suspend fun createCard(@Body card: CardRequest): Response<CardRequest>

    @GET("/cards/{deckId}")
    suspend fun getCards(@Path("deckId") deckId: Int): Response<List<CardRequest>>

}