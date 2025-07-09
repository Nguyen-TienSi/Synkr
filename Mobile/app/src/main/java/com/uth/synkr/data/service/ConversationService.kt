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

    suspend fun getOrCreatePrivateConversation(userId1: String, userId2: String): Conversation {
        val existingConversation = conversationRepository.findPrivateConversationByParticipants(userId1, userId2)
        
        return if (existingConversation != null) {
            existingConversation
        } else {
            val newConversation = Conversation(
                name = "", // Private conversations typically don't have names
                participantIds = listOf(userId1, userId2),
                messageIds = emptyList()
                // conversationType defaults to ConversationType.PRIVATE
            )
            val conversationId = conversationRepository.add(newConversation)
            newConversation.copy(id = conversationId)
        }
    }

    suspend fun findPrivateConversationBetweenUsers(userId1: String, userId2: String): Conversation? {
        return conversationRepository.findPrivateConversationByParticipants(userId1, userId2)
    }
}
