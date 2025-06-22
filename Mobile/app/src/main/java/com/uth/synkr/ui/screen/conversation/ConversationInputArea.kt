package com.uth.synkr.ui.screen.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ConversationInputArea(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onCameraClick: () -> Unit = {},
    onMicClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onCameraClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0F0F0))
        ) {
            Icon(Icons.Filled.CameraAlt, contentDescription = "Camera")
        }
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(
            onClick = onMicClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0F0F0))
        ) {
            Icon(Icons.Filled.Mic, contentDescription = "Mic")
        }
        Spacer(modifier = Modifier.width(4.dp))
        TextField(
            value = message,
            onValueChange = onMessageChange,
            placeholder = { Text("Start typing...") },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF0F0F0),
                focusedContainerColor = Color(0xFFF0F0F0),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            shape = CircleShape,
            singleLine = true
        )
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(
            onClick = onSendClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Filled.Send, contentDescription = "Send", tint = Color.White)
        }
    }
}
