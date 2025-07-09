package com.uth.synkr.ui.screen.auth

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uth.synkr.R
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun SignInScreen(
    authManager: com.uth.synkr.firebase.auth.FirebaseAuthManager,
    onSignInSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    viewModel: SignInViewModel = viewModel { SignInViewModel(authManager) }
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val windowSizeClass = calculateWindowSizeClass(context as Activity)

    val maxWidthModifier = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> Modifier.fillMaxWidth()
        WindowWidthSizeClass.Medium -> Modifier.widthIn(max = 500.dp)
        WindowWidthSizeClass.Expanded -> Modifier.widthIn(max = 700.dp)
        else -> Modifier.fillMaxWidth()
    }

    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show error as snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        contentAlignment = Alignment.Center
    ) {
        // SnackbarHost for error display
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        Column(
            modifier = Modifier
                .padding(16.dp)
                .then(maxWidthModifier),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Sign In", 
                style = MaterialTheme.typography.headlineMedium
            )

            OutlinedTextField(
                value = uiState.email,
                onValueChange = { email -> viewModel.updateEmail(email) },
                placeholder = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.password,
                onValueChange = { password -> viewModel.updatePassword(password) },
                placeholder = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically, 
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = uiState.rememberMe,
                    onCheckedChange = { checked -> viewModel.updateRememberMe(checked) },
                    colors = CheckboxDefaults.colors(checkedColor = Color.Blue)
                )
                Text("Remember me")
            }

            Button(
                onClick = {
                    viewModel.signIn(onSignInSuccess)
                },
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Sign In")
                }
            }

            DividerWithTextCentered("or")

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.signInWithGoogle(onSignInSuccess) },
                    enabled = !uiState.isLoading,
                    shape = CircleShape,
                    contentPadding = PaddingValues(12.dp),
                    modifier = Modifier.size(48.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google",
                        modifier = Modifier.size(20.dp)
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.signInWithFacebook(onSignInSuccess) },
                    enabled = !uiState.isLoading,
                    shape = CircleShape,
                    contentPadding = PaddingValues(12.dp),
                    modifier = Modifier.size(48.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_facebook),
                        contentDescription = "Facebook",
                        modifier = Modifier.size(20.dp)
                    )
                }
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
