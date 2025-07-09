package com.uth.synkr.firebase.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uth.synkr.R
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.security.MessageDigest
import java.util.UUID
import com.google.android.gms.tasks.Task

class FirebaseAuthManager(val context: Context) {

    private val firebaseAuth = Firebase.auth
    private val credentialManager: CredentialManager = CredentialManager.create(context)

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    private fun handleFirebaseAuthTask(task: Task<*>): AuthResponse {
        return if (task.isSuccessful) {
            AuthResponse.Success
        } else {
            AuthResponse.Error(task.exception?.message ?: "Unknown error")
        }
    }

    fun createAccountWithEmail(email: String, password: String): Flow<AuthResponse> = callbackFlow {
        runCatching {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                trySend(handleFirebaseAuthTask(task))
            }
        }.onFailure {
            trySend(AuthResponse.Error(it.message ?: "Error creating account"))
        }
        awaitClose()
    }

    fun signInWithEmail(email: String, password: String): Flow<AuthResponse> = callbackFlow {
        runCatching {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                trySend(handleFirebaseAuthTask(task))
            }
        }.onFailure {
            trySend(AuthResponse.Error(it.message ?: "Error signing in"))
        }
        awaitClose()
    }

    private fun createNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        var bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        bytes = md.digest(bytes)

        return bytes.fold("") { str, it -> str + "%02x".format(it) }
    }

    private fun processGoogleCredential(credential: CustomCredential): Result<Task<*>> {
        return runCatching {
            if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                throw IllegalArgumentException("Unexpected credential type: ${credential.type}")
            }
            
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
            
            firebaseAuth.signInWithCredential(firebaseCredential)
        }
    }

    private fun handleGoogleSignInError(error: Throwable): String {
        return when (error) {
            is ApiException -> {
                Log.e("AuthManager", "Google API Exception: ${error.statusCode} - ${error.message}")
                "Google sign-in failed. Please check your internet connection and try again."
            }
            is GetCredentialException -> {
                Log.e("AuthManager", "Credential Exception: ${error.type} - ${error.message}")
                when {
                    error is NoCredentialException -> "No Google account found. Please add a Google account to your device."
                    error.message?.contains("Unknown calling package") == true -> 
                        "App configuration error. Please contact support."
                    else -> "Google sign-in service unavailable. Please try again later."
                }
            }
            is SecurityException -> {
                Log.e("AuthManager", "Security Exception: ${error.message}")
                "App authentication failed. Please ensure you have the latest version of Google Play Services."
            }
            else -> {
                Log.e("AuthManager", "Unknown Google sign-in error", error)
                error.message ?: "Google sign-in failed. Please try again."
            }
        }
    }

    fun signInWithGoogle(): Flow<AuthResponse> = callbackFlow {
        runCatching {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(false)
                .setNonce(createNonce())
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            Log.d("AuthManager", "Attempting Google sign-in with client ID: ${context.getString(R.string.default_web_client_id)}")
            credentialManager.getCredential(context = context, request = request)
        }.fold(
            onSuccess = { result ->
                Log.d("AuthManager", "Successfully received credential from Google")
                val credential = result.credential
                if (credential is CustomCredential) {
                    processGoogleCredential(credential).fold(
                        onSuccess = { task ->
                            task.addOnCompleteListener {
                                trySend(handleFirebaseAuthTask(it))
                            }
                        },
                        onFailure = { error ->
                            Log.e("AuthManager", "Error processing Google credential", error)
                            trySend(AuthResponse.Error(handleGoogleSignInError(error)))
                        }
                    )
                } else {
                    Log.w("AuthManager", "Credential is not a CustomCredential: ${credential::class.java.name}")
                    trySend(AuthResponse.Error("Unsupported credential format received from Google."))
                }
            },
            onFailure = { error ->
                Log.e("AuthManager", "GetCredential failed", error)
                trySend(AuthResponse.Error(handleGoogleSignInError(error)))
            }
        )

        awaitClose { }
    }

    fun signInWithFacebook(): Flow<AuthResponse> = callbackFlow {
        runCatching {
            val callbackManager = CallbackManager.Factory.create()

            LoginManager.getInstance().registerCallback(
                callbackManager, object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult) {
                        runCatching {
                            val accessToken = result.accessToken
                            val credential = FacebookAuthProvider.getCredential(accessToken.token)
                            firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                                trySend(handleFirebaseAuthTask(task))
                            }
                        }.onFailure { error ->
                            Log.e("AuthManager", "Error processing Facebook credential", error)
                            trySend(AuthResponse.Error(error.message ?: "Error processing Facebook login"))
                        }
                    }

                    override fun onCancel() {
                        trySend(AuthResponse.Error("Facebook sign-in was cancelled."))
                    }

                    override fun onError(error: FacebookException) {
                        trySend(AuthResponse.Error("Facebook sign-in error: ${error.message}"))
                    }
                })

            LoginManager.getInstance().logInWithReadPermissions(
                context as Activity, listOf("public_profile")
            )
        }.onFailure { error ->
            Log.e("AuthManager", "Error initializing Facebook login", error)
            trySend(AuthResponse.Error(error.message ?: "Error starting Facebook login"))
        }

        awaitClose {
            Log.d("FacebookLogin", "signInWithFacebook callbackFlow closing")
        }
    }

    suspend fun signOut() {
        runCatching { 
            firebaseAuth.signOut()
            Log.d("AuthManager", "Firebase sign out successful")
        }.onFailure { 
            Log.e("FirebaseAuthManager", "Error signing out from Firebase", it) 
        }

        runCatching {
            Log.d("AuthManager", "Attempting to clear credential state.")
            val clearRequest = ClearCredentialStateRequest()
            credentialManager.clearCredentialState(clearRequest)
            Log.d("AuthManager", "Credential state cleared successfully.")
        }.onFailure { error ->
            when (error) {
                is ClearCredentialException -> {
                    Log.w("FirebaseAuthManager", "Expected credential clear exception", error)
                }
                else -> {
                    Log.e("FirebaseAuthManager", "Unexpected error clearing credentials", error)
                }
            }
        }
    }
}

interface AuthResponse {
    data object Success : AuthResponse
    data class Error(val message: String) : AuthResponse
}
