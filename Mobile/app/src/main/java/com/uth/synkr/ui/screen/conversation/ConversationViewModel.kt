package com.uth.synkr.ui.screen.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.synkr.data.model.Conversation
import com.uth.synkr.data.model.Message
import com.uth.synkr.data.service.ConversationService
import com.uth.synkr.data.service.MessageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConversationViewModel(
    private val conversationService: ConversationService = ConversationService(),
    private val messageService: MessageService = MessageService()
) : ViewModel() {
    private val _conversation = MutableStateFlow<Conversation?>(null)
    val conversation: StateFlow<Conversation?> = _conversation

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    fun loadConversation(conversationId: String) {
        viewModelScope.launch {
            val conv = conversationService.getConversationById(conversationId)
            _conversation.value = conv
            conv?.let { loadMessages(it) }
        }
    }

    fun loadMessages(conversation: Conversation) {
        viewModelScope.launch {
            _messages.value = messageService.getByConversation(conversation)
        }
    }

    fun sendMessage(conversation: Conversation, senderId: String, content: String) {
        viewModelScope.launch {
            val message = Message(senderId = senderId, content = content)
            val messageId = messageService.createMessage(message)
            // Update conversation's messageIds
            val updatedConv = conversation.copy(messageIds = conversation.messageIds + messageId)
            conversationService.updateConversation(conversation.id!!, updatedConv)
            _conversation.value = updatedConv
            loadMessages(updatedConv)
        }
    }
}

