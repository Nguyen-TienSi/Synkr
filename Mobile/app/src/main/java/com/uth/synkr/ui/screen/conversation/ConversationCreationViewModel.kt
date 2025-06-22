package com.uth.synkr.ui.screen.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.synkr.data.model.Conversation
import com.uth.synkr.data.model.User
import com.uth.synkr.data.model.enumeration.FriendshipStatus
import com.uth.synkr.data.service.ConversationService
import com.uth.synkr.data.service.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConversationCreationViewModel(
    private val userService: UserService = UserService(),
    private val conversationService: ConversationService = ConversationService()
) : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    fun loadFriends(currentUserId: String) {
        viewModelScope.launch {
            val friends = userService.getFriendsOfUser(
                currentUserId,
                listOf(FriendshipStatus.ACCEPTED)
            )
            _users.value = friends
        }
    }

    fun createConversation(currentUserId: String, participants: List<User>, onCreated: (String) -> Unit) {
        viewModelScope.launch {
            val participantIds = (participants.map { it.uid } + currentUserId).toSet().toList()
            val conversation = Conversation(
                participantIds = participantIds,
            )
            val conversationId = conversationService.createConversation(conversation)
            onCreated(conversationId)
        }
    }
}
