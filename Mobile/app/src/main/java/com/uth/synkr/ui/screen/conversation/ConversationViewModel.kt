package com.uth.synkr.ui.screen.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.synkr.data.model.Conversation
import com.uth.synkr.data.model.Message
import com.uth.synkr.data.model.User
import com.uth.synkr.data.model.enumeration.ConversationType
import com.uth.synkr.data.service.ConversationService
import com.uth.synkr.data.service.MessageService
import com.uth.synkr.data.service.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConversationViewModel(
    private val conversationService: ConversationService = ConversationService(),
    private val messageService: MessageService = MessageService(),
    private val userService: UserService = UserService()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ConversationUiState())
    val uiState: StateFlow<ConversationUiState> = _uiState.asStateFlow()

    // Keep existing StateFlows for backward compatibility
    private val _conversation = MutableStateFlow<Conversation?>(null)
    val conversation: StateFlow<Conversation?> = _conversation

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    fun loadConversation(conversationId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val conv = conversationService.getConversationById(conversationId)
                _conversation.value = conv
                _uiState.value = _uiState.value.copy(
                    conversation = conv,
                    isLoading = false,
                    error = null
                )
                conv?.let { 
                    loadMessages(it)
                    loadParticipants(it)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load conversation"
                )
            }
        }
    }

    private fun loadParticipants(conversation: Conversation) {
        viewModelScope.launch {
            try {
                val participants = userService.getUsersByIds(conversation.participantIds)
                _uiState.value = _uiState.value.copy(participants = participants)
            } catch (e: Exception) {
                // Don't fail the entire conversation load for participant loading errors
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load participants: ${e.message}"
                )
            }
        }
    }

    fun getConversationDisplayName(currentUserId: String): String {
        val conversation = _uiState.value.conversation ?: return "Unknown"
        val participants = _uiState.value.participants
        
        return when {
            // If conversation has a name, use it
            conversation.name.isNotBlank() -> conversation.name
            
            // For private conversations, show the other participant's name
            conversation.conversationType == ConversationType.PRIVATE -> {
                val otherParticipant = participants.firstOrNull { it.uid != currentUserId }
                otherParticipant?.fullName ?: "Unknown User"
            }
            
            // For group conversations, show participant names
            conversation.conversationType == ConversationType.GROUP -> {
                if (participants.isNotEmpty()) {
                    participants.joinToString(", ") { it.fullName }
                } else {
                    "Group Chat"
                }
            }
            
            else -> "Unknown"
        }
    }

    fun getConversationSubtitle(currentUserId: String): String {
        val conversation = _uiState.value.conversation ?: return ""
        val participants = _uiState.value.participants
        
        return when (conversation.conversationType) {
            ConversationType.PRIVATE -> {
                val otherParticipant = participants.firstOrNull { it.uid != currentUserId }
                otherParticipant?.email ?: ""
            }
            ConversationType.GROUP -> {
                "${participants.size} members"
            }
        }
    }

    fun loadMessages(conversation: Conversation) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingMessages = true)
            try {
                val messages = messageService.getByConversation(conversation)
                _messages.value = messages
                _uiState.value = _uiState.value.copy(
                    messages = messages,
                    isLoadingMessages = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingMessages = false,
                    error = e.message ?: "Failed to load messages"
                )
            }
        }
    }

    fun sendMessage(conversation: Conversation, senderId: String, content: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSendingMessage = true)
            try {
                val message = Message(senderId = senderId, content = content)
                val messageId = messageService.createMessage(message)
                // Update conversation's messageIds
                val updatedConversation = conversation.copy(messageIds = conversation.messageIds + messageId)
                conversationService.updateConversation(conversation.id!!, updatedConversation)
                _conversation.value = updatedConversation
                _uiState.value = _uiState.value.copy(
                    conversation = updatedConversation,
                    isSendingMessage = false,
                    error = null
                )
                loadMessages(updatedConversation)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSendingMessage = false,
                    error = e.message ?: "Failed to send message"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class ConversationUiState(
    val conversation: Conversation? = null,
    val messages: List<Message> = emptyList(),
    val participants: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMessages: Boolean = false,
    val isSendingMessage: Boolean = false,
    val error: String? = null
)

