package com.example.flashmind.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashmind.data.CardEntity
import com.example.flashmind.data.DeckEntity
import com.example.flashmind.data.FlashcardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FlashcardViewModel(private val repository: FlashcardRepository) : ViewModel() {

    val decks: StateFlow<List<DeckEntity>> = repository.allDecks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getCards(deckId: Int): Flow<List<CardEntity>> {
        return repository.getCardsForDeck(deckId)
    }

    fun createDeck(title: String) {
        viewModelScope.launch {
            repository.addDeck(title)
        }
    }
    fun addCard(deckId: Int, front: String, back: String) {
        viewModelScope.launch {
            repository.addCard(deckId, front, back)
        }
    }

    fun deleteCard(card: CardEntity) {
        viewModelScope.launch {
            repository.deleteCard(card)
        }
    }

    fun deleteDeck(deck: DeckEntity) {
        viewModelScope.launch {
            repository.deleteDeck(deck)
        }
    }

    fun updateCard(card: CardEntity) {
        viewModelScope.launch {
            repository.updateCard(card)
        }
    }
}