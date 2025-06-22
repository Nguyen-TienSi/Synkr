package com.uth.synkr.ui.screen.conversation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uth.synkr.data.model.Message

@Composable
fun ConversationScreen(
    conversationId: String,
    currentUserId: String,
    viewModel: ConversationViewModel = viewModel()
) {
    val conversationState = viewModel.conversation.collectAsState()
    val messagesState = viewModel.messages.collectAsState()
    var message by remember { mutableStateOf("") }

    LaunchedEffect(conversationId) {
        viewModel.loadConversation(conversationId)
    }

    val conversation = conversationState.value
    val messages = messagesState.value

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (conversation != null) {
            ConversationScreenHeader(
                title = conversation.name,
                subtitle = "Last seen on 05:24 PM",
                onBack = { /* handle back */ },
                onCall = { /* handle call */ },
                onVideoCall = { /* handle video call */ }
            )
            LazyColumn(
                modifier = Modifier.weight(1f).padding(8.dp)
            ) {
                items(messages) { msg ->
                    MessageBubble(
                        message = msg,
                        isMe = msg.senderId == currentUserId
                    )
                }
            }
            ConversationInputArea(
                message = message,
                onMessageChange = { message = it },
                onSendClick = {
                    if (message.isNotBlank()) {
                        viewModel.sendMessage(conversation, currentUserId, message)
                        message = ""
                    }
                }
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading conversation...")
            }
        }
    }
}
