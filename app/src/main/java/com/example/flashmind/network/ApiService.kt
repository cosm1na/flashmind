package com.example.flashmind.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.DELETE
import retrofit2.http.PUT

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

    @DELETE("/decks/{id}")
    suspend fun deleteDeck(@Path("id") deckId: Int): Response<ResponseBody>

    @DELETE("/cards/{id}")
    suspend fun deleteCard(@Path("id") cardId: Int): Response<ResponseBody>

    @PUT("/cards/{id}")
    suspend fun updateCard(
        @Path("id") cardId: Int,
        @Body card: CardRequest
    ): Response<ResponseBody>

    @PUT("/decks/{id}")
    suspend fun updateDeck(@Path("id") deckId: Int, @Body request: DeckRequest): Response<ResponseBody>

}