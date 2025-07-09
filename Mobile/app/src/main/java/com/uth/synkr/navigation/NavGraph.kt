package com.uth.synkr.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uth.synkr.firebase.auth.FirebaseAuthManager
import com.uth.synkr.ui.layout.RootScreen
import com.uth.synkr.ui.screen.auth.ForgotPasswordScreen
import com.uth.synkr.ui.screen.auth.SignInScreen
import com.uth.synkr.ui.screen.auth.SignUpScreen
import com.uth.synkr.ui.screen.contact.ContactScreen
import com.uth.synkr.ui.screen.conversation.ConversationCreationScreen
import com.uth.synkr.ui.screen.conversation.ConversationScreen
import com.uth.synkr.ui.screen.home.HomeScreen
import com.uth.synkr.ui.screen.profile.ProfileScreen
import kotlinx.coroutines.launch

@Composable
fun NavGraph(authManager: FirebaseAuthManager) {
    val navController = rememberNavController()
    var currentUser by remember { mutableStateOf(authManager.getCurrentUser()) }
    val coroutineScope = rememberCoroutineScope()

    // Update currentUser when auth state changes
    LaunchedEffect(Unit) {
        currentUser = authManager.getCurrentUser()
    }

    NavHost(
        navController = navController,
        startDestination = if (currentUser != null) AppRoute.HOME else AppRoute.SIGN_IN
    ) {
        composable(AppRoute.SIGN_IN) {
            SignInScreen(
                authManager = authManager,
                onSignInSuccess = {
                    currentUser = authManager.getCurrentUser()
                    navController.navigate(AppRoute.HOME) {
                        popUpTo(AppRoute.SIGN_IN) { inclusive = true }
                    }
                },
                onNavigateToSignUp = { navController.navigate(AppRoute.SIGN_UP) },
                onNavigateToForgotPassword = { navController.navigate(AppRoute.FORGOT_PASSWORD) })
        }
        composable(AppRoute.SIGN_UP) {
            SignUpScreen(authManager = authManager, onSignInSuccess = {
                currentUser = authManager.getCurrentUser()
                navController.navigate(AppRoute.HOME) {
                    popUpTo(AppRoute.SIGN_UP) { inclusive = true }
                }
            }, onNavigateToSignIn = { navController.popBackStack() })
        }
        composable(AppRoute.FORGOT_PASSWORD) {
            ForgotPasswordScreen()
        }
        composable(AppRoute.HOME) {
            currentUser?.let { user ->
                RootScreen(
                    navController = navController,
                    currentRoute = AppRoute.HOME,
                    currentUser = user
                ) {
                    HomeScreen(
                        currentUserId = user.uid,
                        onConversationSelected = { conversationId: String ->
                            navController.navigate("${AppRoute.CONVERSATION}/$conversationId")
                        },
                        onCreateConversation = {
                            navController.navigate(AppRoute.CONVERSATION_CREATION) {
                                launchSingleTop = true
                            }
                        })
                }
            } ?: run {
                // If user is null, navigate back to sign in
                LaunchedEffect(Unit) {
                    navController.navigate(AppRoute.SIGN_IN) {
                        popUpTo(AppRoute.HOME) { inclusive = true }
                    }
                }
            }
        }
        composable(AppRoute.CONTACTS) {
            currentUser?.let { user ->
                RootScreen(
                    navController = navController,
                    currentRoute = AppRoute.CONTACTS,
                    currentUser = user
                ) {
                    ContactScreen(currentUserId = user.uid)
                }
            } ?: run {
                // If user is null, navigate back to sign in
                LaunchedEffect(Unit) {
                    navController.navigate(AppRoute.SIGN_IN) {
                        popUpTo(AppRoute.CONTACTS) { inclusive = true }
                    }
                }
            }
        }
        composable(AppRoute.PROFILE) {
            currentUser?.let { user ->
                RootScreen(
                    navController = navController,
                    currentRoute = AppRoute.PROFILE,
                    currentUser = user
                ) {
                    ProfileScreen(
                        currentUserId = user.uid,
                        onLogout = {
                            coroutineScope.launch {
                                authManager.signOut()
                                currentUser = null
                                navController.navigate(AppRoute.SIGN_IN) {
                                    popUpTo(AppRoute.PROFILE) { inclusive = true }
                                }
                            }
                        },
                        onAccountDetails = { navController.popBackStack() },
                        onSettings = { navController.popBackStack() },
                        onContactUs = { navController.popBackStack() })
                }
            } ?: run {
                // If user is null, navigate back to sign in
                LaunchedEffect(Unit) {
                    navController.navigate(AppRoute.SIGN_IN) {
                        popUpTo(AppRoute.PROFILE) { inclusive = true }
                    }
                }
            }
        }
        composable(AppRoute.CONVERSATION_CREATION) {
            currentUser?.let { user ->
                ConversationCreationScreen(
                    currentUserId = user.uid,
                    onBack = { navController.popBackStack() },
                )
            } ?: run {
                // If user is null, navigate back to sign in
                LaunchedEffect(Unit) {
                    navController.navigate(AppRoute.SIGN_IN) {
                        popUpTo(AppRoute.CONVERSATION_CREATION) { inclusive = true }
                    }
                }
            }
        }
        composable("${AppRoute.CONVERSATION}/{conversationId}") { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            currentUser?.let { user ->
                ConversationScreen(
                    conversationId = conversationId, 
                    currentUserId = user.uid,
                    onBack = { navController.popBackStack() }
                )
            } ?: run {
                // If user is null, navigate back to sign in
                LaunchedEffect(Unit) {
                    navController.navigate(AppRoute.SIGN_IN) {
                        popUpTo("${AppRoute.CONVERSATION}/{conversationId}") { inclusive = true }
                    }
                }
            }
        }
    }
}
