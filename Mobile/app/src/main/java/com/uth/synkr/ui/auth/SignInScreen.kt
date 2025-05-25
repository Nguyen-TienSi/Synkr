package com.uth.synkr.ui.auth

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.uth.synkr.R
import com.uth.synkr.data.firebase.AuthResponse
import com.uth.synkr.data.firebase.FirebaseAuthenticationManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun SignInScreen(
    authManager: FirebaseAuthenticationManager,
    onSignInSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val windowSizeClass = calculateWindowSizeClass(context as Activity)

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val maxWidthModifier = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> Modifier.fillMaxWidth()
        WindowWidthSizeClass.Medium -> Modifier.widthIn(max = 500.dp)
        WindowWidthSizeClass.Expanded -> Modifier.widthIn(max = 700.dp)
        else -> Modifier.fillMaxWidth()
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .then(maxWidthModifier),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error message
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text(
                text = "Sign In",
                style = MaterialTheme.typography.headlineMedium
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = { Text("Username") },
                modifier = Modifier
                    .fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(checkedColor = Color.Blue)
                )
                Text("Remember me")
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        authManager.signInWithEmail(username, password)
                            .collect { response ->
                                when (response) {
                                    is AuthResponse.Success -> onSignInSuccess()
                                    is AuthResponse.Error -> {
                                        errorMessage = response.message
                                        isLoading = false
                                    }
                                }
                            }
                    }
                },
                enabled = !isLoading,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Sign In")
                }
            }

            DividerWithText("or")

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                SocialIconButton(
                    iconRes = R.drawable.ic_google,
                    contentDescription = "Google",
                    onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            authManager.signInWithGoogle()
                                .collect { response ->
                                    when (response) {
                                        is AuthResponse.Success -> onSignInSuccess()
                                        is AuthResponse.Error -> {
                                            errorMessage = response.message
                                            isLoading = false
                                        }
                                    }
                                }
                        }
                    },
                    enabled = !isLoading
                )

                SocialIconButton(
                    iconRes = R.drawable.ic_facebook,
                    contentDescription = "Facebook",
                    onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            authManager.signInWithFacebook()
                                .collect { response ->
                                    when (response) {
                                        is AuthResponse.Success -> onSignInSuccess()
                                        is AuthResponse.Error -> {
                                            errorMessage = response.message
                                            isLoading = false
                                        }
                                    }
                                }
                        }
                    },
                    enabled = !isLoading
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onNavigateToSignUp) {
                    Text("Create an account", color = Color.Black)
                }

                TextButton(onClick = onNavigateToForgotPassword) {
                    Text("Forgot password", color = Color(0xFF007BFF))
                }
            }
        }
    }
}
