package com.example.flashmind.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale // Import nou
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage // Import nou
import com.example.flashmind.R
import com.example.flashmind.SessionManager
import com.example.flashmind.navigation.Screens
import com.example.flashmind.viewmodel.FlashcardViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    sessionManager: SessionManager,
    onBackClick: () -> Unit = {},
    flashcardViewModel: FlashcardViewModel
) {
    val userEmail = sessionManager.getEmail() ?: "utilizator@email.com"
    val initial = userEmail.take(1).uppercase()

    val profilePicUri = sessionManager.getProfilePicUri()
    val username = sessionManager.getUsername()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.purple1))
    ) {
        IconButton(
            onClick = { onBackClick() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 50.dp, start = 16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier.size(30.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, start = 30.dp, end = 30.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(R.drawable.flashcard),
                contentDescription = "flashmind icon",
                modifier = Modifier.size(50.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.purple2)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(colorResource(R.color.purple1)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!profilePicUri.isNullOrEmpty()) {
                            AsyncImage(
                                model = profilePicUri,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = initial,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        // Arată username-ul dacă l-a setat, altfel scrie "Connected Account"
                        text = if (!username.isNullOrEmpty()) username else "Connected Account",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Text(
                        text = userEmail,
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = { navController.navigate(Screens.ManageAccount.route) },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(45.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.purple1)
                        )
                    ) {
                        Text(
                            text = "manage account",
                            color = Color.Black,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    flashcardViewModel.clearDatabase()
                    sessionManager.logout()
                    navController.navigate(Screens.Login.route) {
                        popUpTo(0)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(45.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                elevation = ButtonDefaults.buttonElevation(2.dp)
            ) {
                Text(
                    text = "LOG OUT",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Flashmind App v1.0.0",
                fontSize = 12.sp,
                color = Color.Black.copy(alpha = 0.5f)
            )
        }
    }
}