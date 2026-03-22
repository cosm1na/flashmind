package com.example.flashmind.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Insert
    suspend fun insertDeck(deck: DeckEntity)

    @Query("SELECT * FROM decks")
    fun getAllDecks(): Flow<List<DeckEntity>>

    @Delete
    suspend fun deleteDeck(deck: DeckEntity)

    @Insert
    suspend fun insertCard(card: CardEntity)

    @Query("SELECT * FROM cards WHERE deckId = :deckId")
    fun getCardsForDeck(deckId: Int): Flow<List<CardEntity>>
    @Delete
    suspend fun deleteCard(card: CardEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCard(card: CardEntity)

    @Update
    suspend fun updateDeck(deck: DeckEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDecks(decks: List<DeckEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<CardEntity>)

    @Query("DELETE FROM decks")
    suspend fun clearAll()

    @Query("SELECT * FROM cards WHERE deckId = :deckId AND nextReviewDate <= :currentTime")
    fun getCardsToReview(deckId: Int, currentTime: Long): Flow<List<CardEntity>>

}