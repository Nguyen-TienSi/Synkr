package com.uth.synkr.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uth.synkr.model.User
import com.uth.synkr.ui.components.FriendItem

@Composable
fun SuggestedFriendsScreen(users: List<User>, onAdd: (User) -> Unit, onDelete: (User) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Gợi ý kết bạn", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        users.forEach {
            FriendItem(it, "Thêm bạn", { onAdd(it) }, { onDelete(it) })
        }
    }
}
