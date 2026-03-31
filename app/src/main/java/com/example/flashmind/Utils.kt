package com.example.flashmind

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import kotlinx.coroutines.delay


fun Modifier.bounceClick(onClick: () -> Unit): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "bounce_anim"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { onClick() }
        )
        .pointerInput(isPressed) {
            awaitPointerEventScope {
                isPressed = if (isPressed) {
                    waitForUpOrCancellation()
                    false
                } else {
                    awaitFirstDown(requireUnconsumed = false)
                    true
                }
            }
        }
}

@Composable
fun EmptyStateAnimation(mesaj: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty_state))
        val progress by animateLottieCompositionAsState(
            composition,
            iterations = LottieConstants.IterateForever
        )

        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = mesaj,
            color = Color.Gray,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StudyFinishedState(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.cat_nap))
        val progress by animateLottieCompositionAsState(
            composition,
            iterations = LottieConstants.IterateForever
        )

        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(250.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "You're a genius!",
            color = Color.Black,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 28.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Time for a nap?",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(50.dp)
                .bounceClick { onBackClick() },
            shape = RoundedCornerShape(50),
            color = colorResource(R.color.purple2),
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "Back to Decks",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun CooldownTimerScreen(
    lastFinishedTime: Long,
    cooldownMinutes: Int = 10,
    onTimerFinished: () -> Unit,
    onBackClick: () -> Unit
) {
    val targetTime = lastFinishedTime + (cooldownMinutes * 60 * 1000)
    var timeLeftMillis by remember { mutableLongStateOf(targetTime - System.currentTimeMillis()) }

    LaunchedEffect(key1 = timeLeftMillis) {
        if (timeLeftMillis > 0) {
            delay(1000)
            timeLeftMillis -= 1000
        } else {
            onTimerFinished()
        }
    }

    val minutes = (timeLeftMillis / 1000) / 60
    val seconds = (timeLeftMillis / 1000) % 60

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Take a break! ☕",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 32.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = String.format("%02d:%02d", minutes, seconds),
            fontSize = 64.sp,
            fontWeight = FontWeight.ExtraBold,
            color = colorResource(R.color.purple2)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "until you can study this deck again",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(50.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(50.dp)
                .bounceClick { onBackClick() },
            shape = RoundedCornerShape(50),
            color = colorResource(R.color.purple2),
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "Back to Decks",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
