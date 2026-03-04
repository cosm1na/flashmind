package com.example.flashmind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.flashmind.data.AppDatabase
import com.example.flashmind.data.FlashcardRepository
import com.example.flashmind.navigation.AppNavHost
import com.example.flashmind.ui.theme.FlashmindTheme
import com.example.flashmind.viewmodel.FlashcardViewModel
import com.example.flashmind.viewmodel.FlashcardViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this)

        val repository = FlashcardRepository(database.flashcardDao())

        setContent {
            FlashmindTheme {
                val viewModel: FlashcardViewModel = viewModel(
                    factory = FlashcardViewModelFactory(repository)
                )

                val navController = rememberNavController()

                AppNavHost(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}