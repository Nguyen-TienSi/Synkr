package com.uth.synkr.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.uth.synkr.data.model.Conversation
import com.uth.synkr.data.model.User
import com.uth.synkr.data.model.enumeration.ConversationType
import com.uth.synkr.data.service.UserService

@Composable
fun ConversationItem(
    conversation: Conversation,
    lastMessage: String?,
    currentUserId: String,
    onClick: () -> Unit,
    userService: UserService = UserService()
) {
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var displayName by remember { mutableStateOf("") }
    var displayImage by remember { mutableStateOf("") }

    LaunchedEffect(conversation.participantIds) {
        users = userService.getUsersByIds(conversation.participantIds)
        
        // Determine display name
        displayName = when {
            conversation.name.isNotBlank() -> conversation.name
            conversation.conversationType == ConversationType.PRIVATE -> {
                val otherUser = users.firstOrNull { it.uid != currentUserId }
                otherUser?.fullName ?: "Unknown User"
            }
            else -> users.joinToString(", ") { it.fullName }
        }
        
        // Determine display image
        displayImage = when {
            conversation.conversationType == ConversationType.PRIVATE -> {
                val otherUser = users.firstOrNull { it.uid != currentUserId }
                otherUser?.pictureUrl ?: ""
            }
            else -> users.firstOrNull()?.pictureUrl ?: ""
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
            // Profile Image or Group Icon
            ConversationImage(
                imageUrl = displayImage,
                conversationType = conversation.conversationType,
                users = users,
                currentUserId = currentUserId
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = lastMessage ?: "No messages yet",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
    }
}

@Composable
private fun ConversationImage(
    imageUrl: String,
    conversationType: ConversationType,
    users: List<User>,
    currentUserId: String
) {
    when (conversationType) {
        ConversationType.PRIVATE -> {
            // Show single user image
            if (imageUrl.isNotBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback for missing image
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    val otherUser = users.firstOrNull { it.uid != currentUserId }
                    Text(
                        text = otherUser?.fullName?.take(1)?.uppercase() ?: "?",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
        ConversationType.GROUP -> {
            // Show group icon or multiple user images
            if (users.size <= 2) {
                // Single image for small groups
                val firstUser = users.firstOrNull()
                if (firstUser != null && firstUser.pictureUrl.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(firstUser.pictureUrl),
                        contentDescription = "Group picture",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Fallback group icon
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "G",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            } else {
                // Multiple user images in a grid for larger groups
                GroupImageGrid(users = users.take(4))
            }
        }
    }
}

@Composable
private fun GroupImageGrid(users: List<User>) {
    Box(
        modifier = Modifier.size(56.dp)
    ) {
        when (users.size) {
            2 -> {
                Row(
                    modifier = Modifier.size(56.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    users.take(2).forEach { user ->
                        Image(
                            painter = rememberAsyncImagePainter(user.pictureUrl),
                            contentDescription = "Member picture",
                            modifier = Modifier
                                .size(27.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            3 -> {
                Column(
                    modifier = Modifier.size(56.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(users[0].pictureUrl),
                        contentDescription = "Member picture",
                        modifier = Modifier
                            .size(27.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray, CircleShape)
                            .align(Alignment.CenterHorizontally),
                        contentScale = ContentScale.Crop
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        users.drop(1).take(2).forEach { user ->
                            Image(
                                painter = rememberAsyncImagePainter(user.pictureUrl),
                                contentDescription = "Member picture",
                                modifier = Modifier
                                    .size(27.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
            else -> {
                // 4 or more users in a 2x2 grid
                Column(
                    modifier = Modifier.size(56.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        users.take(2).forEach { user ->
                            Image(
                                painter = rememberAsyncImagePainter(user.pictureUrl),
                                contentDescription = "Member picture",
                                modifier = Modifier
                                    .size(27.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        users.drop(2).take(2).forEach { user ->
                            Image(
                                painter = rememberAsyncImagePainter(user.pictureUrl),
                                contentDescription = "Member picture",
                                modifier = Modifier
                                    .size(27.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}
