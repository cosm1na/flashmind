package com.example.flashmind.data

import kotlinx.coroutines.flow.Flow

class FlashcardRepository(private val dao: FlashcardDao) {
    val allDecks: Flow<List<DeckEntity>> = dao.getAllDecks()

    suspend fun addDeck(title: String) {
        dao.insertDeck(DeckEntity(title = title))
    }
    suspend fun addCard(deckId: Int, front: String, back: String) {
        dao.insertCard(CardEntity(deckId = deckId, frontText = front, backText = back))
    }
    fun getCardsForDeck(deckId: Int): Flow<List<CardEntity>> = dao.getCardsForDeck(deckId)

    suspend fun deleteCard(card: CardEntity) {
        dao.deleteCard(card)
    }

    suspend fun deleteDeck(deck: DeckEntity) {
        dao.deleteDeck(deck)
    }

    suspend fun updateCard(card: CardEntity) {
        dao.updateCard(card)
    }

    suspend fun updateDeck(deck: DeckEntity){
        dao.updateDeck(deck)
    }

    suspend fun clearLocalDatabase() {
        dao.clearAll()
    }

    suspend fun saveDecksFromServer(decks: List<DeckEntity>) {
        dao.insertDecks(decks)
    }

    suspend fun saveCardsFromServer(cards: List<CardEntity>) {
        dao.insertCards(cards)
    }

//    fun getCardsToReview(deckId: Int): Flow<List<CardEntity>> {
//        return dao.getCardsToReview(deckId, System.currentTimeMillis())
//    }
}