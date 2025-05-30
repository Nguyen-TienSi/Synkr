package com.uth.synkr.ui.friend

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.uth.synkr.model.User
import androidx.compose.ui.Alignment

@Composable
fun FriendItem(
    user: User,
    buttonLabel: String,
    onAdd: () -> Unit,
    onDelete: () -> Unit
) {
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
        Button(onClick = onAdd, colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)) {
            Text(buttonLabel, color = Color.White)
        }
        Spacer(Modifier.width(8.dp))
        Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
            Text("Xóa")
        }
    }
}
