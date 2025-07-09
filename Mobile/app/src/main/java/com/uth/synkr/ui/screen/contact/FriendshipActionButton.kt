package com.uth.synkr.ui.screen.contact

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class FriendshipAction {
    ADD, ACCEPT, REJECT, UNFRIEND, CANCEL
}

@Composable
fun FriendshipActionButton(
    action: FriendshipAction, 
    onClick: () -> Unit, 
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (action) {
        FriendshipAction.ADD -> "Add" to Color(0xFF007BFF)
        FriendshipAction.ACCEPT -> "Accept" to Color(0xFF28A745)
        FriendshipAction.REJECT -> "Reject" to Color(0xFFDC3545)
        FriendshipAction.UNFRIEND -> "Unfriend" to Color(0xFF007BFF)
        FriendshipAction.CANCEL -> "Cancel" to Color.Gray
    }
    
    Button(
        onClick = onClick, 
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = color, 
            contentColor = Color.White
        ), 
        modifier = modifier
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(text)
        }
    }
}
