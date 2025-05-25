package com.uth.synkr

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uth.synkr.data.firebase.FirebaseAuthenticationManager
import com.uth.synkr.ui.auth.ForgotPasswordScreen
import com.uth.synkr.ui.auth.SignInScreen
import com.uth.synkr.ui.auth.SignUpScreen
import com.uth.synkr.ui.home.HomeScreen

@Composable
fun NavigationManager(authManager: FirebaseAuthenticationManager) {
    val navController = rememberNavController()
    val currentUser by remember { mutableStateOf(authManager.getCurrentUser()) }

    NavHost(
        navController = navController,
        startDestination = if (currentUser != null) "home" else "signIn"
    ) {
        composable("signIn") {
            SignInScreen(
                authManager = authManager,
                onSignInSuccess = {
                    navController.navigate("home") {
                        popUpTo("signIn") { inclusive = true }
                    }
                },
                onNavigateToSignUp = { navController.navigate("signUp") },
                onNavigateToForgotPassword = { navController.navigate("forgotPassword") }
            )
        }
        composable("signUp") {
            SignUpScreen(
                authManager = authManager,
                onSignInSuccess = {
                    navController.navigate("home") {
                        popUpTo("signUp") { inclusive = true }
                    }
                },
                onNavigateToSignIn = { navController.popBackStack() }
            )
        }
        composable("forgotPassword") {
            ForgotPasswordScreen()
        }
        composable("home") {
            HomeScreen(
                authManager = authManager,
                onSignOut = {
                    navController.navigate("signIn") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
