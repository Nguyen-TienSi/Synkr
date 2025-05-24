package com.uth.synkr.ui.friend

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uth.synkr.model.User

@Composable
fun FriendRequestsScreen(users: List<User>, onAccept: (User) -> Unit, onDelete: (User) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Lời mời kết bạn", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        users.forEach {
            FriendItem(it, "Chấp nhận", { onAccept(it) }, { onDelete(it) })
        }
    }
}
