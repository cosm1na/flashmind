package com.example.flashmind.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashmind.data.CardEntity
import com.example.flashmind.data.DeckEntity
import com.example.flashmind.data.FlashcardRepository
import com.example.flashmind.network.CardRequest
import com.example.flashmind.network.DeckRequest
import com.example.flashmind.network.RetrofitClient
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

    fun createDeck(title: String, userId: Int) {
        viewModelScope.launch {
            try {
                if (userId != -1) {
                    val pachet = DeckRequest(userId = userId, titlu = title)
                    val raspuns = RetrofitClient.api.createDeck(pachet)

                    if (raspuns.isSuccessful) {
                        Log.d("SERVER_SYNC", "Succes! Deck-ul a ajuns pe server!")
                    } else {
                        Log.e("SERVER_SYNC", "Eroare la server: ${raspuns.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("SERVER_SYNC", "Fără internet, salvăm doar pe telefon.")
            }

            repository.addDeck(title)
        }
    }

    fun addCard(deckId: Int, front: String, back: String) {
        viewModelScope.launch {
            try {
                val pachetCard = CardRequest(
                    deckId = deckId,
                    fata = front,
                    spate = back
                )

                // Îl trimitem
                val raspuns = RetrofitClient.api.createCard(pachetCard)

                if (raspuns.isSuccessful) {
                    Log.d("SERVER_SYNC", "Succes! Cartonașul a ajuns pe server!")
                } else {
                    Log.e("SERVER_SYNC", "Eroare la server: ${raspuns.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("SERVER_SYNC", "Fără internet, salvăm doar pe telefon.")
            }

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