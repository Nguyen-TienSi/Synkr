package com.uth.synkr.ui.screen.auth

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uth.synkr.R
import com.uth.synkr.firebase.auth.FirebaseAuthManager
import com.uth.synkr.constant.SocialProvider
import com.uth.synkr.ui.screen.auth.DividerWithTextCentered
import com.uth.synkr.ui.screen.auth.PasswordRequirement
import androidx.compose.runtime.remember

@Composable
fun SignUpScreen(
    authManager: FirebaseAuthManager,
    onSignInSuccess: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    viewModel: SignUpViewModel = viewModel { SignUpViewModel(authManager) }
) {
    val uiState by viewModel.uiState.collectAsState()
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
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        // SnackbarHost for error display
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Create Account", style = MaterialTheme.typography.headlineMedium
            )

            // Name field
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.updateName(it) },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                })

            // Email field
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.updateEmail(it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null)
                })

            // Phone field
            OutlinedTextField(
                value = uiState.phone,
                onValueChange = { viewModel.updatePhone(it) },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null)
                })

            // Password field
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { viewModel.updatePasswordVisible(!uiState.passwordVisible) }) {
                        Icon(
                            if (uiState.passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (uiState.passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp)
            )

            // Confirm Password field
            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = { viewModel.updateConfirmPassword(it) },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (uiState.confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { viewModel.updateConfirmPasswordVisible(!uiState.confirmPasswordVisible) }) {
                        Icon(
                            if (uiState.confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (uiState.confirmPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp)
            )

            // Password requirements
            val passwordGuideNeeded = uiState.password.isNotBlank() && (
                uiState.password.length < 8 ||
                !uiState.password.any { it.isDigit() } ||
                uiState.password == uiState.password.lowercase() ||
                uiState.password != uiState.confirmPassword ||
                uiState.confirmPassword.isBlank()
            )
            if (passwordGuideNeeded) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    PasswordRequirement(
                        met = uiState.password.length >= 8, text = "At least 8 characters"
                    )
                    PasswordRequirement(
                        met = uiState.password.any { it.isDigit() }, text = "Contains a number"
                    )
                    PasswordRequirement(
                        met = uiState.password != uiState.password.lowercase(), text = "Contains uppercase"
                    )
                    PasswordRequirement(
                        met = uiState.password == uiState.confirmPassword && uiState.confirmPassword.isNotBlank(),
                        text = "Passwords match"
                    )
                }
            }

            // Sign Up button (triggers Firebase Auth + Firestore user creation)
            Button(
                onClick = {
                    viewModel.signUp(onSignInSuccess)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                enabled = viewModel.isFormValid && !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp
                    )
                } else {
                    Text("Sign Up")
                }
            }

            DividerWithTextCentered("or")

            // Social login buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.OutlinedButton(
                    onClick = { viewModel.signUpWithGoogle(onSignInSuccess) },
                    enabled = !uiState.isLoading,
                    shape = androidx.compose.foundation.shape.CircleShape,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
                    modifier = Modifier.size(48.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_google),
                        contentDescription = "Sign up with Google",
                        modifier = Modifier.size(20.dp)
                    )
                }

                androidx.compose.material3.OutlinedButton(
                    onClick = { viewModel.signUpWithFacebook(onSignInSuccess) },
                    enabled = !uiState.isLoading,
                    shape = androidx.compose.foundation.shape.CircleShape,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
                    modifier = Modifier.size(48.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_facebook),
                        contentDescription = "Sign up with Facebook",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Already have an account? Sign in
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Already have an account?")
                TextButton(onClick = onNavigateToSignIn) {
                    Text("Sign in", color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
