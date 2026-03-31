package com.example.flashmind.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashmind.R
import com.example.flashmind.data.DeckEntity
import com.example.flashmind.network.uploadPdfBytesToServer
import kotlinx.coroutines.launch
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.flashmind.bounceClick

@Composable
fun DeckListScreen(
    userId: Int,
    decks: List<DeckEntity>,
    onRefreshDecks: (Int) -> Unit = {},
    onCreateDeckClick: () -> Unit = {},
    onDeckClick: (Int) -> Unit = {},
    onDeleteDeckClick: (DeckEntity) -> Unit = {},
    onEditDeckClick: (DeckEntity, String) -> Unit = { _, _ -> },
    onSettingsClick: () -> Unit = {}
) {
    var deckToEdit by remember { mutableStateOf<DeckEntity?>(null) }
    var newDeckName by remember { mutableStateOf("") }
    var deckToDelete by remember { mutableStateOf<DeckEntity?>(null) }

    var pdfBytesToUpload by remember { mutableStateOf<ByteArray?>(null) }
    var pdfDeckName by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }


    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            if (bytes != null) {
                pdfBytesToUpload = bytes
                pdfDeckName = ""
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.purple1))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(70.dp))

            Image(
                painter = painterResource(R.drawable.flashcard),
                contentDescription = "flashmind icon",
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(
                    items = decks,
                    key = { _, deck -> deck.id }
                ) { index, deck ->
                    DeckCard(
                        title = deck.title,
                        index = index,
                        onClick = { onDeckClick(deck.id) },
                        onDeleteClick = { deckToDelete = deck },
                        onEditClick = {
                            deckToEdit = deck
                            newDeckName = deck.title
                        }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { pdfLauncher.launch("application/pdf") },
            containerColor = Color.White,
            contentColor = colorResource(R.color.purple2),
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 90.dp)
                .size(56.dp),
            elevation = FloatingActionButtonDefaults.elevation(6.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.PictureAsPdf,
                contentDescription = "Upload PDF",
                modifier = Modifier.size(28.dp)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(colorResource(R.color.purple2))
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { onCreateDeckClick() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
            ) {
                Text(
                    text = "create deck",
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                onClick = { onSettingsClick() },
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = Color.Black,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }

    if (deckToEdit != null) {
        AlertDialog(
            onDismissRequest = { deckToEdit = null },
            title = { Text(text = "Edit Deck Name", fontWeight = FontWeight.Bold, color = Color.Black) },
            text = {
                OutlinedTextField(
                    value = newDeckName,
                    onValueChange = { newDeckName = it },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newDeckName.isNotBlank()) {
                        onEditDeckClick(deckToEdit!!, newDeckName)
                        deckToEdit = null
                    }
                }) {
                    Text("Save", color = colorResource(R.color.purple2), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deckToEdit = null }) { Text("Cancel", color = Color.Gray) }
            }
        )
    }

    if (deckToDelete != null) {
        AlertDialog(
            onDismissRequest = { deckToDelete = null },
            title = { Text(text = "Delete Deck", fontWeight = FontWeight.Bold, color = Color.Black) },
            text = { Text(text = "Are you sure you want to delete '${deckToDelete?.title}'?", color = Color.DarkGray) },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteDeckClick(deckToDelete!!)
                    deckToDelete = null
                }) { Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { deckToDelete = null }) { Text("Cancel", color = Color.Gray) }
            }
        )
    }

    if (pdfBytesToUpload != null) {
        AlertDialog(
            onDismissRequest = { pdfBytesToUpload = null },
            title = { Text(text = "Nume Pachet Nou", fontWeight = FontWeight.Bold, color = Color.Black) },
            text = {
                OutlinedTextField(
                    value = pdfDeckName,
                    onValueChange = { pdfDeckName = it },
                    label = { Text("Ex: Curs Istorie") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (pdfDeckName.isNotBlank() && pdfBytesToUpload != null) {
                        val bytesToSend = pdfBytesToUpload!!
                        val nameToSend = pdfDeckName

                        pdfBytesToUpload = null
                        isGenerating = true

                        coroutineScope.launch {
                            try {
                                uploadPdfBytesToServer(bytesToSend, nameToSend, userId)
                                onRefreshDecks(userId)
                            } catch (e: Exception) {
                                println("Eroare la upload: ${e.message}")
                            } finally {
                                isGenerating = false
                            }
                        }
                    }
                }) { Text("Generează din PDF", color = colorResource(R.color.purple2), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { pdfBytesToUpload = null }) { Text("Anulează", color = Color.Gray) }
            }
        )
    }

    if (isGenerating) {
        Dialog(
            onDismissRequest = { /* Nu lăsăm utilizatorul să închidă dând click pe lângă */ },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ai_loading))
                    val progress by animateLottieCompositionAsState(
                        composition,
                        iterations = LottieConstants.IterateForever
                    )

                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(120.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "AI-ul generează...",
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.purple2)
                    )
                }
            }
        }
    }
}

@Composable
fun DeckCard(
    title: String,
    index: Int,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isEven = index % 2 == 0
    val paddingStart = if (isEven) 30.dp else 60.dp
    val paddingEnd = if (isEven) 60.dp else 30.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = paddingStart, end = paddingEnd, bottom = 16.dp)
            .bounceClick() { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(colorResource(R.color.purple2).copy(alpha = 0.2f))
                        .clickable { onEditClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Deck",
                        tint = colorResource(R.color.purple2),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray.copy(alpha = 0.3f))
                        .clickable { onDeleteClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Deck",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}