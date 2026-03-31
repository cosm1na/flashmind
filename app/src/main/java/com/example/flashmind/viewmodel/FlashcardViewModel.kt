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

    fun deleteDeck(deck: DeckEntity) {
        viewModelScope.launch {
            try {
                val raspuns = RetrofitClient.api.deleteDeck(deck.id)
                if (raspuns.isSuccessful) {
                    Log.d("SERVER", "Deck șters de pe server.")
                } else {
                    Log.e("SERVER", "Eroare: ${raspuns.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("SERVER", "Fără net, nu se poate șterge de pe server.")
            }
            repository.deleteDeck(deck)
        }
    }

    fun deleteCard(card: CardEntity) {
        viewModelScope.launch {
            try {
                val raspuns = RetrofitClient.api.deleteCard(card.id)
                if (raspuns.isSuccessful) {
                    Log.d("SERVER", "Card șters de pe server.")
                }
            } catch (e: Exception) {
                Log.e("SERVER", "Fără net.")
            }
            repository.deleteCard(card)
        }
    }

    fun updateCard(card: CardEntity) {
        viewModelScope.launch {
            try {
                val pachetNou = CardRequest(
                    deckId = card.deckId,
                    fata = card.frontText,
                    spate = card.backText
                )
                val raspuns = RetrofitClient.api.updateCard(card.id, pachetNou)
                if (raspuns.isSuccessful) {
                    Log.d("SERVER", "Card actualizat pe server.")
                }
            } catch (e: Exception) {
                Log.e("SERVER", "Fără net.")
            }

            repository.updateCard(card)
        }
    }

    fun updateDeck(deck: DeckEntity, newTitle: String, userId: Int) {
        Log.d("TEST_EDIT", "Am ajuns în ViewModel! Nume nou: $newTitle")
        viewModelScope.launch {
            try {
                val pachetNou = DeckRequest(userId = userId, titlu = newTitle)
                val raspuns = RetrofitClient.api.updateDeck(deck.id, pachetNou)

                if (raspuns.isSuccessful) {
                    Log.d("SERVER_SYNC", "Deck actualizat cu succes pe server.")
                }
            } catch (e: Exception) {
                Log.e("SERVER_SYNC", "Fără net, se modifică doar local.")
            }

            val deckActualizat = deck.copy(title = newTitle)
            repository.updateDeck(deckActualizat)
        }
    }

    fun clearDatabase() {
        viewModelScope.launch {
            repository.clearLocalDatabase()
        }
    }

    fun loadDecks(userId: Int) {
        viewModelScope.launch {
            try {
                val raspunsDecks = RetrofitClient.api.getDecks(userId)

                if (raspunsDecks.isSuccessful) {
                    val pacheteDeLaServer = raspunsDecks.body() ?: emptyList()

                    repository.clearLocalDatabase()

                    val pachetePentruRoom = pacheteDeLaServer.map { pachetNet ->
                        DeckEntity(
                            id = pachetNet.id ?: 0,
                            title = pachetNet.titlu
                        )
                    }

                    repository.saveDecksFromServer(pachetePentruRoom)

                    for (pachet in pacheteDeLaServer) {
                        val pachetIdReal = pachet.id ?: continue
                        val raspunsCards = RetrofitClient.api.getCards(pachetIdReal)

                        if (raspunsCards.isSuccessful) {
                            val cartonaseDeLaServer = raspunsCards.body() ?: emptyList()

                            val cartonasePentruRoom = cartonaseDeLaServer.map { cardNet ->
                                CardEntity(
                                    id = cardNet.id ?: 0,
                                    deckId = cardNet.deckId,
                                    frontText = cardNet.fata,
                                    backText = cardNet.spate
                                )
                            }
                            repository.saveCardsFromServer(cartonasePentruRoom)
                        }
                    }
                    Log.d("SYNC", "Sincronizarea datelor s-a terminat cu succes!")
                }
            } catch (e: Exception) {
                Log.e("SYNC", "Fara net sau eroare la sincronizare: ${e.message}")
            }
        }
    }

    fun evalueazaCartonas(card: CardEntity, aStiut: Boolean) {
        viewModelScope.launch {
            val nouaCutie: Int
            val intervalMinute: Long

            if (aStiut) {
                nouaCutie = if (card.boxNumber < 5) card.boxNumber + 1 else 5

                intervalMinute = when (nouaCutie) {
                    1 -> 0L
                    2 -> 2L
                    3 -> 5L
                    4 -> 15L
                    5 -> 60L
                    else -> 0L
                }
            } else {
                nouaCutie = 1
                intervalMinute = 0L
            }

            val milisecundeInMinut = 60 * 1000L
            val dataViitoareRepetare = System.currentTimeMillis() + (intervalMinute * milisecundeInMinut)

            val cardActualizat = card.copy(
                boxNumber = nouaCutie,
                nextReviewDate = dataViitoareRepetare
            )

            repository.updateCard(cardActualizat)

            try {
                val request = CardRequest(
                    deckId = cardActualizat.deckId,
                    fata = cardActualizat.frontText,
                    spate = cardActualizat.backText,
                    cutie = cardActualizat.boxNumber,
                    dataViitoare = cardActualizat.nextReviewDate
                )
                RetrofitClient.api.updateCard(cardActualizat.id, request)
            } catch (e: Exception) {
                Log.e("LEITNER", "Fără net, salvăm progresul doar local.")
            }
        }
    }

    fun markDeckAsFinished(deck: DeckEntity) {
        viewModelScope.launch {
            val updatedDeck = deck.copy(lastFinishedTimestamp = System.currentTimeMillis())
            repository.updateDeck(updatedDeck)
        }
    }
}