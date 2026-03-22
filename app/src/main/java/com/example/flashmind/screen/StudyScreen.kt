package com.example.flashmind.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flashmind.R
import com.example.flashmind.data.CardEntity

@Composable
fun StudyScreen(
    navController: NavController,
    deckName: String = "Study",
    cards: List<CardEntity>,
    onBackClick: () -> Unit = {},
    onEvaluateCard: (CardEntity, Boolean) -> Unit = { _, _ -> }
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    val isFinished = currentIndex >= cards.size

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.purple2))
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
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(70.dp))

            Image(
                painter = painterResource(R.drawable.flashcard),
                contentDescription = "flashmind icon",
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = deckName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (!isFinished && cards.isNotEmpty()) {
                Text(
                    text = "${currentIndex + 1} / ${cards.size}",
                    fontSize = 16.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            if (cards.isEmpty()) {
                Text("No cards to study right now!", fontSize = 18.sp, color = Color.Black)
            } else if (isFinished) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = " Good job! \n You've finished this session.",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    Button(
                        onClick = { onBackClick() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("back to deck", color = Color.Black)
                    }
                }
            } else {
                val currentCard = cards[currentIndex]

                val rotation by animateFloatAsState(
                    targetValue = if (isFlipped) 180f else 0f,
                    animationSpec = tween(durationMillis = 400),
                    label = "flipAnimation"
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(350.dp)
                        .clickable { isFlipped = !isFlipped }
                        .graphicsLayer {
                            rotationY = rotation
                            cameraDistance = 12f * density
                        },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (rotation <= 90f) Color.White else colorResource(R.color.purple1)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (rotation <= 90f) {
                            Text(
                                text = currentCard.frontText,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(20.dp)
                            )
                        } else {
                            Text(
                                text = currentCard.backText,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(20.dp)
                                    .graphicsLayer { rotationY = 180f }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                if (isFlipped) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 50.dp, start = 30.dp, end = 30.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = {
                                onEvaluateCard(currentCard, false)
                                isFlipped = false
                                currentIndex++
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.weight(1f).height(55.dp),
                            elevation = ButtonDefaults.buttonElevation(2.dp)
                        ) {
                            Text(
                                text = "AGAIN ",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        Button(
                            onClick = {
                                onEvaluateCard(currentCard, true)
                                isFlipped = false
                                currentIndex++
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.weight(1f).height(55.dp),
                            elevation = ButtonDefaults.buttonElevation(2.dp)
                        ) {
                            Text(
                                text = "GOT IT ",
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                } else {
                    Text(
                        text = "click the card to see the answer",
                        color = Color.DarkGray,
                        modifier = Modifier.padding(bottom = 60.dp)
                    )
                }
            }
        }
    }
}