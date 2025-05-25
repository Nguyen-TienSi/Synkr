package com.uth.synkr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.auth.FirebaseAuth
import com.uth.synkr.data.firebase.FirebaseAuthenticationManager

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
            MaterialTheme {
                NavigationManager(authManager)
            }
        }
    }
}
