package com.uth.synkr.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uth.synkr.firebase.auth.FirebaseAuthManager
import com.uth.synkr.ui.layout.RootScreen
import com.uth.synkr.ui.screen.auth.ForgotPasswordScreen
import com.uth.synkr.ui.screen.auth.SignInScreen
import com.uth.synkr.ui.screen.auth.SignUpScreen
import com.uth.synkr.ui.screen.contact.ContactScreen
import com.uth.synkr.ui.screen.home.HomeScreen
import com.uth.synkr.ui.screen.profile.ProfileScreen
import com.uth.synkr.ui.screen.conversation.ConversationCreationScreen
import com.uth.synkr.ui.screen.conversation.ConversationScreen
import kotlinx.coroutines.launch

@Composable
fun NavGraph(authManager: FirebaseAuthManager) {
    val navController = rememberNavController()
    val currentUser by remember { mutableStateOf(authManager.getCurrentUser()) }
    val coroutineScope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = if (currentUser != null) AppRoute.HOME else AppRoute.SIGN_IN
    ) {
        composable(AppRoute.SIGN_IN) {
            SignInScreen(
                authManager = authManager,
                onSignInSuccess = {
                    navController.navigate(AppRoute.HOME) {
                        popUpTo(AppRoute.SIGN_IN) { inclusive = true }
                    }
                },
                onNavigateToSignUp = { navController.navigate(AppRoute.SIGN_UP) },
                onNavigateToForgotPassword = { navController.navigate(AppRoute.FORGOT_PASSWORD) })
        }
        composable(AppRoute.SIGN_UP) {
            SignUpScreen(authManager = authManager, onSignInSuccess = {
                navController.navigate(AppRoute.HOME) {
                    popUpTo(AppRoute.SIGN_UP) { inclusive = true }
                }
            }, onNavigateToSignIn = { navController.popBackStack() })
        }
        composable(AppRoute.FORGOT_PASSWORD) {
            ForgotPasswordScreen()
        }
        composable(AppRoute.HOME) {
            RootScreen(
                navController = navController,
                currentRoute = AppRoute.HOME,
                currentUser = currentUser
            ) {
                HomeScreen(
                    currentUserId = currentUser!!.uid,
                    navController = navController
                )
            }
        }
        composable(AppRoute.CONTACTS) {
            RootScreen(
                navController = navController,
                currentRoute = AppRoute.CONTACTS,
                currentUser = currentUser
            ) {
                ContactScreen(currentUserId = currentUser!!.uid)
            }
        }
        composable(AppRoute.PROFILE) {
            RootScreen(
                navController = navController,
                currentRoute = AppRoute.PROFILE,
                currentUser = currentUser
            ) {
                ProfileScreen(
                    onLogout = {
                    coroutineScope.launch {
                        authManager.signOut()
                        navController.navigate(AppRoute.SIGN_IN) {
                            popUpTo(AppRoute.PROFILE) { inclusive = true }
                        }
                    }
                },
                    onAccountDetails = { navController.popBackStack() },
                    onSettings = { navController.popBackStack() },
                    onContactUs = { navController.popBackStack() })
            }
        }
        composable(AppRoute.CONVERSATION_CREATION) {
            ConversationCreationScreen(
                currentUserId = currentUser!!.uid,
                onBack = { navController.popBackStack() },
            )
        }
        composable("${AppRoute.CONVERSATION}/{conversationId}") { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            ConversationScreen(
                conversationId = conversationId,
                currentUserId = currentUser!!.uid
            )
        }
    }
}
