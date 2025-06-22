package com.uth.synkr.ui.screen.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.uth.synkr.data.model.User
import com.uth.synkr.data.model.enumeration.UserStatus
import com.uth.synkr.data.service.UserService
import kotlinx.coroutines.launch

@Composable
fun SocialIconButton(
    iconRes: Int,
    contentDescription: String,
    enabled: Boolean = true,
    authManager: com.uth.synkr.firebase.auth.FirebaseAuthManager,
    onSignInSuccess: () -> Unit,
    setLoading: (Boolean) -> Unit,
    setError: (String?) -> Unit,
    provider: SocialProvider
) {
    val coroutineScope = rememberCoroutineScope()

    OutlinedButton(
        enabled = enabled,
        onClick = {
            coroutineScope.launch {
                setLoading(true)
                val flow = when (provider) {
                    SocialProvider.GOOGLE -> authManager.signInWithGoogle()
                    SocialProvider.FACEBOOK -> authManager.signInWithFacebook()
                }
                flow.collect { response ->
                    when (response) {
                        is com.uth.synkr.firebase.auth.AuthResponse.Success -> {
                            val firebaseUser = FirebaseAuth.getInstance().currentUser
                            firebaseUser?.let { user ->
                                val userService = UserService()
                                val existing = userService.getByEmail(user.email ?: "")
                                if (existing == null) {
                                    val newUser = User(
                                        uid = user.uid,
                                        fullName = user.displayName ?: "",
                                        pictureUrl = user.photoUrl?.toString() ?: "",
                                        email = user.email ?: "",
                                        status = UserStatus.ONLINE,
                                        lastSeen = Timestamp.now()
                                    )
                                    userService.createUser(newUser)
                                }
                            }
                            setLoading(false)
                            onSignInSuccess()
                        }

                        is com.uth.synkr.firebase.auth.AuthResponse.Error -> {
                            setError(response.message)
                            setLoading(false)
                        }
                    }
                }
            }
        },
        shape = CircleShape,
        contentPadding = PaddingValues(12.dp),
        modifier = Modifier.size(48.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp)
        )
    }
}

enum class SocialProvider {
    GOOGLE, FACEBOOK
}
