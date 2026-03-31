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
import com.example.flashmind.data.DeckEntity
import com.example.flashmind.viewmodel.FlashcardViewModel
import com.example.flashmind.bounceClick
import com.example.flashmind.StudyFinishedState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StudyScreen(
    navController: NavController,
    deck: DeckEntity,
    allCards: List<CardEntity>,
    viewModel: FlashcardViewModel,
    onBackClick: () -> Unit = {},
    onEvaluateCard: (CardEntity, Boolean) -> Unit = { _, _ -> }
) {
    var snapshotTaken by remember { mutableStateOf(false) }
    var cardsToStudy by remember { mutableStateOf<List<CardEntity>>(emptyList()) }

    LaunchedEffect(allCards) {
        if (!snapshotTaken && allCards.isNotEmpty()) {
            cardsToStudy = allCards.filter { it.nextReviewDate <= System.currentTimeMillis() }
            snapshotTaken = true
        }
    }

    val nextAvailableDate = remember(allCards) {
        allCards.filter { it.nextReviewDate > System.currentTimeMillis() }
            .minOfOrNull { it.nextReviewDate }
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    val isFinished = currentIndex >= cardsToStudy.size && snapshotTaken && cardsToStudy.isNotEmpty()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.purple2))
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 50.dp, start = 16.dp)
                .size(48.dp)
                .bounceClick { onBackClick() },
            contentAlignment = Alignment.Center
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
            if (!snapshotTaken && allCards.isNotEmpty()) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 200.dp))
            }
            else if (snapshotTaken && cardsToStudy.isEmpty()) {
                Spacer(modifier = Modifier.height(150.dp))

                Text(
                    text = "You're all caught up!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (nextAvailableDate != null) {
                    SrsCountdownTimer(
                        targetTimestamp = nextAvailableDate,
                        onTimerFinished = {
                            snapshotTaken = false
                        }
                    )
                } else {
                    Text(text = "Add more cards to keep studying!", color = Color.DarkGray, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(50.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(50.dp)
                        .bounceClick { onBackClick() },
                    shape = RoundedCornerShape(50),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Back to Decks",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
            else if (isFinished) {
                StudyFinishedState(onBackClick = onBackClick)
            }
            else if (cardsToStudy.isNotEmpty()) {
                Spacer(modifier = Modifier.height(70.dp))

                Image(
                    painter = painterResource(R.drawable.flashcard),
                    contentDescription = "flashmind icon",
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = deck.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "${currentIndex + 1} / ${cardsToStudy.size}",
                    fontSize = 16.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(50.dp))

                val currentCard = cardsToStudy[currentIndex]

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
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(55.dp)
                                .bounceClick {
                                    onEvaluateCard(currentCard, false)
                                    isFlipped = false
                                    coroutineScope.launch {
                                        delay(200)
                                        currentIndex++
                                    }
                                },
                            shape = RoundedCornerShape(50),
                            color = Color.White,
                            shadowElevation = 2.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "AGAIN ",
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(55.dp)
                                .bounceClick {
                                    onEvaluateCard(currentCard, true)
                                    isFlipped = false
                                    coroutineScope.launch {
                                        delay(200)
                                        currentIndex++
                                    }
                                },
                            shape = RoundedCornerShape(50),
                            color = Color.White,
                            shadowElevation = 2.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "GOT IT ",
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
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
@Composable
fun SrsCountdownTimer(targetTimestamp: Long, onTimerFinished: () -> Unit) {
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val remainingMillis = targetTimestamp - currentTime
    val secondsTotal = (remainingMillis / 1000).coerceAtLeast(0)

    val hours = secondsTotal / 3600
    val minutes = (secondsTotal % 3600) / 60
    val seconds = secondsTotal % 60

    LaunchedEffect(key1 = currentTime) {
        if (remainingMillis > 0) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        } else {
            onTimerFinished()
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Next card ready in:", fontSize = 18.sp, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (hours > 0) String.format("%02d:%02d:%02d", hours, minutes, seconds)
            else String.format("%02d:%02d", minutes, seconds),
            fontSize = 54.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )
    }
}