package com.uth.synkr.ui.screen.conversation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.uth.synkr.R
import com.uth.synkr.data.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationCreationScreen(
    viewModel: ConversationCreationViewModel = viewModel(),
    currentUserId: String,
    onBack: () -> Unit = {},
    onCreateGroup: (List<User>) -> Unit = {}
) {
    val users by viewModel.users.collectAsState()
    var selectedUsers by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(currentUserId) {
        viewModel.loadFriends(currentUserId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Choose People") }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                }
            }, actions = {
                TextButton(
                    onClick = {
                        val selected = users.filter { selectedUsers.contains(it.uid) }
                        viewModel.createConversation(currentUserId, selected) { conversationId ->
                            // Optionally navigate or show a message
                            onCreateGroup(selected)
                        }
                    }) {
                    Text("Create Group")
                }
            })
        }) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding, modifier = Modifier.fillMaxSize()
        ) {
            items(users) { user ->
                UserListItem(
                    user = user, selected = selectedUsers.contains(user.uid), onClick = {
                        selectedUsers = if (selectedUsers.contains(user.uid)) {
                            selectedUsers - user.uid
                        } else {
                            selectedUsers + user.uid
                        }
                    })
            }
        }
    }
}

@Composable
fun UserListItem(
    user: User, selected: Boolean, onClick: () -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        // Avatar
        if (user.pictureUrl.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(user.pictureUrl),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.sample_profile_pic),
                contentDescription = "Avatar",
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        // Name
        Text(
            text = user.fullName, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)
        )
        // Checkmark if selected
        if (selected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = Color.Black
            )
        }
    }
}
