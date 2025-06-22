package com.uth.synkr.ui.screen.conversation

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreenHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit = {},
    onCall: () -> Unit = {},
    onVideoCall: () -> Unit = {}
) {
    TopAppBar(navigationIcon = {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
    }, title = {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }, actions = {
        IconButton(onClick = onCall) {
            Icon(Icons.Filled.Call, contentDescription = "Call")
        }
        IconButton(onClick = onVideoCall) {
            Icon(Icons.Filled.Videocam, contentDescription = "Video Call")
        }
    })
}

