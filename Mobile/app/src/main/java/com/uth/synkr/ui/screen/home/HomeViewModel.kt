package com.uth.synkr.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.synkr.data.model.Conversation
import com.uth.synkr.data.service.ConversationService
import com.uth.synkr.data.service.UserService
import com.uth.synkr.data.service.MessageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ConversationWithLastMessage(
    val conversation: Conversation,
    val lastMessage: String?
)

class HomeViewModel(
    private val conversationService: ConversationService = ConversationService(),
    val userService: UserService = UserService(),
    private val messageService: MessageService = MessageService()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadConversations(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val conversations = conversationService.getConversationsByParticipant(userId)
                val conversationsWithLastMessage = conversations.map { conversation ->
                    val messages = messageService.getByConversation(conversation)
                    val lastMessage = messages.maxByOrNull { it.timestamp }?.content
                    ConversationWithLastMessage(conversation, lastMessage)
                }
                _uiState.value = _uiState.value.copy(
                    conversations = conversationsWithLastMessage,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load conversations"
                )
            }
        }
    }
    
    fun refreshConversations(userId: String) {
        loadConversations(userId)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class HomeUiState(
    val conversations: List<ConversationWithLastMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false
)
