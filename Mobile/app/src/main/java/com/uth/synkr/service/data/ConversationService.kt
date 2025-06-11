package com.uth.synkr.service.data

import com.uth.synkr.data.firebase.repository.ConversationRepository
import com.uth.synkr.model.Conversation

class ConversationService(
    private val conversationRepository: ConversationRepository = ConversationRepository()
) {
    suspend fun createConversation(conversation: Conversation): String {
        return conversationRepository.add(conversation)
    }

    suspend fun updateConversation(id: String, conversation: Conversation) {
        conversationRepository.update(id, conversation)
    }

    suspend fun deleteConversation(id: String) {
        conversationRepository.delete(id)
    }

    suspend fun getConversation(id: String): Conversation? {
        return conversationRepository.get(id)
    }

    suspend fun getAllConversations(): List<Conversation> {
        return conversationRepository.getAll()
    }

    suspend fun getConversationsByParticipant(userId: String): List<Conversation> {
        return conversationRepository.getByParticipant(userId)
    }
}
