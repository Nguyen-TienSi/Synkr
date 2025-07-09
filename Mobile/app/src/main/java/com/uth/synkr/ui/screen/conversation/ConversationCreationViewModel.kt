package com.uth.synkr.ui.screen.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.synkr.data.model.Conversation
import com.uth.synkr.data.model.User
import com.uth.synkr.data.model.enumeration.ConversationType
import com.uth.synkr.data.model.enumeration.FriendshipStatus
import com.uth.synkr.data.service.ConversationService
import com.uth.synkr.data.service.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConversationCreationViewModel(
    private val userService: UserService = UserService(),
    private val conversationService: ConversationService = ConversationService()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ConversationCreationUiState())
    val uiState: StateFlow<ConversationCreationUiState> = _uiState.asStateFlow()

    // Keep existing StateFlow for backward compatibility
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    fun loadFriends(currentUserId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val friends = userService.getFriendsOfUser(
                    currentUserId,
                    listOf(FriendshipStatus.ACCEPTED)
                )
                _users.value = friends
                _uiState.value = _uiState.value.copy(
                    friends = friends,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load friends"
                )
            }
        }
    }

    fun createConversation(currentUserId: String, participants: List<User>, onCreated: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreating = true, error = null)
            try {
                val participantIds = (participants.map { it.uid } + currentUserId).toSet().toList()
                val conversation = Conversation(
                    participantIds = participantIds,
                    conversationType = ConversationType.GROUP
                )
                val conversationId = conversationService.createConversation(conversation)
                _uiState.value = _uiState.value.copy(
                    isCreating = false,
                    error = null
                )
                onCreated(conversationId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCreating = false,
                    error = e.message ?: "Failed to create conversation"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class ConversationCreationUiState(
    val friends: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val isCreating: Boolean = false,
    val error: String? = null
)
