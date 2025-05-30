package com.uth.synkr.ui.friend

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.uth.synkr.model.User
import androidx.compose.ui.Alignment

@Composable
fun FriendsListScreen(users: List<User>, onCall: (User) -> Unit, onVideoCall: (User) -> Unit) {
    Text("    Bạn bè", style = MaterialTheme.typography.titleLarge)
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(users) { user ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(user.pictureUrl),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp).clip(CircleShape)
                )
                Spacer(Modifier.width(8.dp))
                Text(user.fullName, modifier = Modifier.weight(1f))
                IconButton(onClick = { onCall(user) }) {
                    Icon(Icons.Default.Call, contentDescription = "Call")
                }
                IconButton(onClick = { onVideoCall(user) }) {
                    Icon(Icons.Default.Videocam, contentDescription = "Video")
                }
            }
        }
    }
}
