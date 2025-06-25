package com.uth.synkr.ui.screen.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(
    currentUserId: String,
    onConversationSelected: (String) -> Unit,
    onCreateConversation: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val conversationsState = viewModel.conversations.collectAsState()
    val conversations = conversationsState.value

    LaunchedEffect(currentUserId) {
        viewModel.loadConversations(currentUserId)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateConversation) {
                Icon(Icons.Default.Add, contentDescription = "Add Conversation")
            }
        }) { innerPadding ->
        LazyColumn(contentPadding = innerPadding) {
            items(conversations) { conversation ->
                ConversationItem(conversation) {
                    onConversationSelected(conversation.id ?: "")
                }
            }
        }
    }
}
