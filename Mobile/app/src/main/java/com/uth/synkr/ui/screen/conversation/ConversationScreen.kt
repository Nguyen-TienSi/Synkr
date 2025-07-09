package com.uth.synkr.ui.screen.conversation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uth.synkr.data.model.Message

@Composable
fun ConversationScreen(
    conversationId: String,
    currentUserId: String,
    onBack: () -> Unit = {},
    viewModel: ConversationViewModel = viewModel { ConversationViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var message by remember { mutableStateOf("") }

    LaunchedEffect(conversationId) {
        viewModel.loadConversation(conversationId)
    }

    // Show error snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            uiState.conversation != null -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ConversationScreenHeader(
                        title = viewModel.getConversationDisplayName(currentUserId),
                        subtitle = viewModel.getConversationSubtitle(currentUserId),
                        onBack = onBack,
                        onCall = { /* handle call */ },
                        onVideoCall = { /* handle video call */ }
                    )
                    
                    if (uiState.isLoadingMessages) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f).padding(8.dp)
                        ) {
                            items(uiState.messages) { msg ->
                                MessageBubble(
                                    message = msg,
                                    isMe = msg.senderId == currentUserId
                                )
                            }
                        }
                    }
                    
                    ConversationInputArea(
                        message = message,
                        onMessageChange = { message = it },
                        onSendClick = {
                            if (message.isNotBlank() && !uiState.isSendingMessage) {
                                viewModel.sendMessage(uiState.conversation!!, currentUserId, message)
                                message = ""
                            }
                        }
                    )
                }
            }
            
            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No conversation data available",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
