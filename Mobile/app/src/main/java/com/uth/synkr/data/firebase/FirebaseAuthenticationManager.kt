package com.uth.synkr.data.firebase

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
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

class FirebaseAuthenticationManager(val context: Context) {

    private val firebaseAuth = Firebase.auth
    private val credentialManager: CredentialManager = CredentialManager.create(context)

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun createAccountWithEmail(email: String, password: String): Flow<AuthResponse> = callbackFlow {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySend(AuthResponse.Success)
                } else {
                    trySend(AuthResponse.Error(task.exception?.message ?: "Unknown error"))
                }
            }
        awaitClose()
    }

    fun signInWithEmail(email: String, password: String): Flow<AuthResponse> = callbackFlow {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySend(AuthResponse.Success)
                } else {
                    trySend(AuthResponse.Error(task.exception?.message ?: "Unknown error"))
                }
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

    fun signInWithGoogle(): Flow<AuthResponse> = callbackFlow {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .setAutoSelectEnabled(false)
            .setNonce(createNonce())
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result = credentialManager.getCredential(
                context = context,
                request = request
            )

            val credential = result.credential
            if (credential is CustomCredential) {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val firebaseCredential = GoogleAuthProvider
                            .getCredential(
                                googleIdTokenCredential.idToken,
                                null
                            )
                        firebaseAuth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    trySend(AuthResponse.Success)
                                } else {
                                    trySend(
                                        AuthResponse.Error(
                                            task.exception?.message ?: "Unknown error"
                                        )
                                    )
                                }
                            }
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("AuthManager", "Google ID Token Parsing Exception", e)
                        trySend(AuthResponse.Error(e.message ?: "Google ID Token parsing error"))
                    }
                } else {
                    Log.w("AuthManager", "Credential type not Google ID Token: ${credential.type}")
                    trySend(AuthResponse.Error("Unexpected credential type received."))
                }
            } else {
                Log.w(
                    "AuthManager",
                    "Credential is not a CustomCredential: ${credential::class.java.name}"
                )
                trySend(AuthResponse.Error("Credential format not supported."))
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "GetCredential Exception", e)
            trySend(AuthResponse.Error(e.message ?: "Error signing in with Google."))
        }

        awaitClose { }
    }

    fun signInWithFacebook(): Flow<AuthResponse> = callbackFlow {
        val callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    val accessToken = loginResult.accessToken
                    val credential = FacebookAuthProvider.getCredential(accessToken.token)

                    firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                trySend(AuthResponse.Success)
                            } else {
                                trySend(
                                    AuthResponse.Error(
                                        task.exception?.message ?: "Firebase sign-in failed."
                                    )
                                )
                            }
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
            context as Activity,
            listOf(
//                "email",
                "public_profile"
            )
        )

        awaitClose {
            Log.d("FacebookLogin", "signInWithFacebook callbackFlow closing")
        }
    }

    suspend fun signOut() {
        firebaseAuth.signOut()

        try {
            Log.d("AuthManager", "Attempting to clear credential state.")
            val clearRequest = ClearCredentialStateRequest()
            credentialManager.clearCredentialState(clearRequest)
            Log.d("AuthManager", "Credential state cleared successfully.")
        } catch (e: ClearCredentialException) {
            Log.e("FirebaseAuthenticationManager", "Error clearing credentials", e)
        } catch (e: Exception) {
            Log.e(
                "FirebaseAuthenticationManager",
                "Unexpected error during sign out credential clearing",
                e
            )
        }
    }
}

interface AuthResponse {
    data object Success : AuthResponse
    data class Error(val message: String) : AuthResponse
}
