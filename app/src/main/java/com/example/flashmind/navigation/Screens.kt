package com.example.flashmind.navigation


sealed class Screens(val route: String) {
    data object Splash : Screens("splash_screen")
    data object Welcome : Screens("welcome_screen")
    data object Login : Screens("login_screen")
    data object CreateAccount : Screens("create_account_screen")
    data object DeckList : Screens("deck_list_screen")
    data object CreateDeck : Screens("create_deck_screen")
    data object ViewCards : Screens("view_cards_screen/{deckId}/{deckName}") {
        fun createRoute(deckId: Int, deckName: String) = "view_cards_screen/$deckId/$deckName"
    }
    data object AddCard : Screens("add_card_screen/{deckId}") {
        fun createRoute(deckId: Int) = "add_card_screen/$deckId"
    }
    data object Study : Screens("study_screen/{deckId}/{deckName}") {
        fun createRoute(deckId: Int, deckName: String) = "study_screen/$deckId/$deckName"
    }

    data object Settings : Screens ("settings_screen")
}