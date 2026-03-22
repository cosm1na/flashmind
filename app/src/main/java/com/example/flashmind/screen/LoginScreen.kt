package com.example.flashmind.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flashmind.R
import com.example.flashmind.SessionManager
import com.example.flashmind.navigation.Screens
import kotlinx.coroutines.launch

import com.example.flashmind.network.AuthRequest
import com.example.flashmind.network.RetrofitClient
import com.example.flashmind.viewmodel.FlashcardViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    flashcardViewModel: FlashcardViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.purple1))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 70.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.flashcard),
                contentDescription = "flashmind icon",
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.height(110.dp))

            Card(
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
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
                    Text(
                        text = "Please login to continue using the app",
                        color = Color.Black,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(30.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("email") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(30.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = {
                            if (email.isNotEmpty() && password.isNotEmpty()) {
                                coroutineScope.launch {
                                    try {
                                        val pachet = AuthRequest(email, password)
                                        val raspuns = RetrofitClient.api.loginUser(pachet)

                                        if (raspuns.isSuccessful) {
                                            val pachetLogin = raspuns.body()

                                            if (pachetLogin != null && pachetLogin.succes) {
                                                Toast.makeText(context, pachetLogin.mesaj, Toast.LENGTH_SHORT).show()

                                                sessionManager.salveazaStareaDeLogin(true)
                                                sessionManager.saveEmail(email)

                                                pachetLogin.username?.let { sessionManager.saveUsername(it) }
                                                pachetLogin.pozaProfil?.let { sessionManager.saveProfilePicUri(it) }

                                                if (pachetLogin.userId != null) {
                                                    sessionManager.salveazaUserId(pachetLogin.userId)

                                                    flashcardViewModel.sincronizeazaDateDePeServer(pachetLogin.userId)
                                                }

                                                navController.navigate(Screens.DeckList.route) {
                                                    popUpTo(Screens.Login.route) { inclusive = true }
                                                }
                                            } else {
                                                val mesajEroare = pachetLogin?.mesaj ?: "Eroare de autentificare!"
                                                Toast.makeText(context, mesajEroare, Toast.LENGTH_LONG).show()
                                            }
                                        } else {
                                            Toast.makeText(context, "Eroare de conexiune cu serverul!", Toast.LENGTH_LONG).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Nu mă pot conecta la server!", Toast.LENGTH_LONG).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Scrie emailul și parola!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text(
                            text = stringResource(R.string.login),
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                text = "Don’t have an account?",
                color = Color.Black,
                fontSize = 15.sp
            )
            Button(
                onClick = {
                    navController.navigate(Screens.CreateAccount.route)
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.sign_up),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }
        }
    }
}
