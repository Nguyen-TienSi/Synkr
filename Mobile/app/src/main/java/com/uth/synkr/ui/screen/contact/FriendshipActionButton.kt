package com.uth.synkr.ui.screen.contact

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

enum class FriendshipAction {
    ADD, UNFRIEND, CANCEL
}

@Composable
fun FriendshipActionButton(
    action: FriendshipAction, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    val (text, color) = when (action) {
        FriendshipAction.ADD -> "Add Friend" to Color(0xFF007BFF)
        FriendshipAction.UNFRIEND -> "Unfriend" to Color(0xFF007BFF)
        FriendshipAction.CANCEL -> "Cancel" to Color.Gray
    }
    Button(
        onClick = onClick, colors = ButtonDefaults.buttonColors(
            containerColor = color, contentColor = Color.White
        ), modifier = modifier
    ) {
        Text(text)
    }
}
