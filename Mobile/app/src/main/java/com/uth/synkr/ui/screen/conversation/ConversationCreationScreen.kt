package com.uth.synkr.ui.screen.conversation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.uth.synkr.R
import com.uth.synkr.data.model.User
import com.uth.synkr.ui.screen.conversation.UserListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationCreationScreen(
    viewModel: ConversationCreationViewModel = viewModel { ConversationCreationViewModel() },
    currentUserId: String,
    onBack: () -> Unit = {},
    onCreateGroup: (List<User>) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val users by viewModel.users.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedUsers by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(currentUserId) {
        viewModel.loadFriends(currentUserId)
    }

    // Show error snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose People") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (!uiState.isCreating) {
                                val selected = users.filter { selectedUsers.contains(it.uid) }
                                viewModel.createConversation(currentUserId, selected) { _ ->
                                    onCreateGroup(selected)
                                }
                            }
                        },
                        enabled = selectedUsers.isNotEmpty() && !uiState.isCreating
                    ) {
                        if (uiState.isCreating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Create Group")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            uiState.friends.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No friends available\nAdd some friends to create a group",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            else -> {
                LazyColumn(
                    contentPadding = innerPadding,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(users) { user ->
                        UserListItem(
                            user = user,
                            selected = selectedUsers.contains(user.uid),
                            onClick = {
                                selectedUsers = if (selectedUsers.contains(user.uid)) {
                                    selectedUsers - user.uid
                                } else {
                                    selectedUsers + user.uid
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
