package com.uth.synkr.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.synkr.data.model.Conversation
import com.uth.synkr.data.service.ConversationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val conversationService: ConversationService = ConversationService()
) : ViewModel() {
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations

    fun loadConversations(userId: String) {
        viewModelScope.launch {
            _conversations.value = conversationService.getConversationsByParticipant(userId)
        }
    }
}
