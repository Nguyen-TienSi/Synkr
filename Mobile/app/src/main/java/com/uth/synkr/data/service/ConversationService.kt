package com.uth.synkr.data.service

import com.uth.synkr.data.model.Conversation
import com.uth.synkr.data.repository.ConversationRepository

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

    suspend fun getConversationById(id: String): Conversation? {
        return conversationRepository.get(id)
    }

    suspend fun getConversationsByParticipant(userId: String): List<Conversation> {
        return conversationRepository.getByParticipant(userId)
    }
}
