package com.uth.synkr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.auth.FirebaseAuth
import com.uth.synkr.data.firebase.FirebaseAuthenticationManager
import com.uth.synkr.ui.profile.EditProfileScreen
import com.uth.synkr.ui.profile.SettingsScreen
import com.uth.synkr.ui.theme.Synkr1562Theme

class MainActivity : ComponentActivity() {
    private lateinit var authManager: FirebaseAuthenticationManager
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FacebookSdk.setAutoInitEnabled(true)
        FacebookSdk.fullyInitialize()
        AppEventsLogger.activateApp(application)

        firebaseAuth = FirebaseAuth.getInstance()
        authManager = FirebaseAuthenticationManager(this)


        setContent {
//            MaterialTheme {
//                ChatScreen("ekegggggggggggggggkek", null)
//            }
            val (isDarkModeEnabled, setDarkModeEnabled) = remember { mutableStateOf(false) }
            // Khởi tạo NavController
            val navController = rememberNavController()

            Synkr1562Theme(darkTheme = isDarkModeEnabled) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Định nghĩa NavHost để quản lý các màn hình
                    NavHost(navController = navController, startDestination = "settings_screen") {
                        composable("settings_screen") {
                            SettingsScreen(
                                isDarkModeEnabled = isDarkModeEnabled,
                                onDarkModeToggle = setDarkModeEnabled,
                                onNavigateToEditProfile = {
                                    navController.navigate("edit_profile_screen")
                                }
                            )
                        }
                        composable("edit_profile_screen") {
                            EditProfileScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
