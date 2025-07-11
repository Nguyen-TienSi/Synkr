package com.uth.synkr.ui.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.uth.synkr.data.model.User

@Composable
fun ProfileScreen(
    currentUserId: String,
    onLogout: () -> Unit = {},
    onAccountDetails: () -> Unit = {},
    onSettings: () -> Unit = {},
    onContactUs: () -> Unit = {},
    onChangeAvatar: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel { ProfileViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(currentUserId) {
        viewModel.loadUserProfile(currentUserId)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            uiState.user != null -> {
                ProfileContent(
                    user = uiState.user!!,
                    onLogout = onLogout,
                    onAccountDetails = onAccountDetails,
                    onSettings = onSettings,
                    onContactUs = onContactUs,
                    onChangeAvatar = onChangeAvatar
                )
            }
            
            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No user data available",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    user: User,
    onLogout: () -> Unit,
    onAccountDetails: () -> Unit,
    onSettings: () -> Unit,
    onContactUs: () -> Unit,
    onChangeAvatar: () -> Unit
) {
    // Profile Picture with Edit Button
    Box(
        modifier = Modifier
            .size(120.dp)
            .padding(top = 16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        if (user.pictureUrl.isNotBlank()) {
            Image(
                painter = rememberAsyncImagePainter(user.pictureUrl),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray, CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            // Fallback for missing profile picture
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.fullName.take(1).uppercase(),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        
        IconButton(
            onClick = onChangeAvatar,
            modifier = Modifier
                .offset(x = (-8).dp, y = (-8).dp)
                .size(36.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Change Avatar",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
    
    Spacer(modifier = Modifier.height(12.dp))
    
    Text(
        text = user.fullName.ifBlank { "Unknown User" },
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurface
    )
    
    if (user.email.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = user.email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    
    Spacer(modifier = Modifier.height(24.dp))

    // Menu Items
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
    ) {
        ProfileMenuItem(
            icon = Icons.Filled.AccountCircle,
            text = "Account Details",
            onClick = onAccountDetails
        )
        ProfileMenuItem(
            icon = Icons.Filled.Settings, 
            text = "Settings", 
            onClick = onSettings
        )
        ProfileMenuItem(
            icon = Icons.Filled.Call,
            text = "Contact Us",
            onClick = onContactUs,
            iconTint = Color(0xFF4CAF50),
            textColor = Color.Gray
        )
    }

    Spacer(modifier = Modifier.height(32.dp))

    // Logout Button
    OutlinedButton(
        onClick = onLogout,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = "Logout", 
            color = MaterialTheme.colorScheme.onSurface, 
            fontSize = 16.sp
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text, 
            color = textColor, 
            fontSize = 16.sp
        )
    }
}

