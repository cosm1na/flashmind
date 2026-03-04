package com.example.flashmind.navigation

import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.flashmind.screen.AddCardScreen
import com.example.flashmind.screen.CreateAccount
import com.example.flashmind.screen.CreateDeckScreen
import com.example.flashmind.screen.SplashScreen
import com.example.flashmind.screen.WelcomeScreen
import com.example.flashmind.screen.LoginScreen
import com.example.flashmind.screen.DeckListScreen
import com.example.flashmind.screen.StudyScreen
import com.example.flashmind.screen.ViewCardsScreen
import com.example.flashmind.viewmodel.FlashcardViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Screens.Splash.route,
    viewModel: FlashcardViewModel
) {
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screens.Splash.route) { SplashScreen(navController) }
        composable(Screens.Welcome.route) { WelcomeScreen(navController) }

        composable(Screens.Login.route) {
            LoginScreen(
                navController = navController,
                onLoginClick = { email, password ->
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        navController.navigate(Screens.DeckList.route) {
                            popUpTo(Screens.Login.route) { inclusive = true }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Please enter your email and password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }

        composable(Screens.CreateAccount.route) {
            CreateAccount(
                navController = navController,
                onSignUpClick = { email, password ->
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        navController.navigate(Screens.DeckList.route) {
                            popUpTo(Screens.CreateAccount.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screens.DeckList.route) {
            val decks by viewModel.decks.collectAsState()

            DeckListScreen(
                decks = decks,
                onCreateDeckClick = {
                    navController.navigate(Screens.CreateDeck.route)
                },
                onDeckClick = { deckId ->
                    val selectedDeck = decks.find { it.id == deckId }
                    val selectedDeckName = selectedDeck?.title ?: "Unknown Deck"

                    navController.navigate(Screens.ViewCards.createRoute(deckId, selectedDeckName))                },
                onBackClick = {
                    navController.navigate(Screens.Welcome.route) {
                        popUpTo(0)
                    }
                },
                onDeleteDeckClick = { deckToDelete ->
                    viewModel.deleteDeck(deckToDelete)
                }
            )
        }

        composable(Screens.CreateDeck.route) {
            CreateDeckScreen(
                navController = navController,
                onCreateClick = { deckName ->
                    viewModel.createDeck(deckName)

                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screens.ViewCards.route,
            arguments = listOf(
                navArgument("deckId") { type = NavType.IntType },
                navArgument("deckName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getInt("deckId") ?: 0
            val deckName = backStackEntry.arguments?.getString("deckName") ?: "Unknown Deck"

            val cards by viewModel.getCards(deckId).collectAsState(initial = emptyList())

            ViewCardsScreen(
                navController = navController,
                deckName = deckName,
                cards = cards,
                onAddCardsClick = {
                    navController.navigate(Screens.AddCard.createRoute(deckId))
                },
                onStudyClick = {
                    val safeDeckName = Uri.encode(deckName)
                    navController.navigate(Screens.Study.createRoute(deckId, safeDeckName))
                },
                onBackClick = { navController.popBackStack(Screens.DeckList.route, inclusive = false) },
                onDeleteCardClick = { card ->
                    viewModel.deleteCard(card)
                },
                onEditCardClick = { updatedCard ->
                    viewModel.updateCard(updatedCard)
                }
            )
        }

        composable(
            route = Screens.AddCard.route,
            arguments = listOf(navArgument("deckId") { type = NavType.IntType })
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getInt("deckId") ?: 0

            AddCardScreen(
                navController = navController,
                onSaveCardClick = { frontText, backText ->
                    viewModel.addCard(deckId, frontText, backText)

                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screens.Study.route,
            arguments = listOf(
                navArgument("deckId") { type = NavType.IntType },
                navArgument("deckName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getInt("deckId") ?: 0
            val deckName = backStackEntry.arguments?.getString("deckName") ?: "Study"

            val cards by viewModel.getCards(deckId).collectAsState(initial = emptyList())

            StudyScreen(
                navController = navController,
                deckName = deckName,
                cards = cards,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}