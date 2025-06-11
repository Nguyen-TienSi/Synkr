package com.uth.synkr.service.data

import com.uth.synkr.data.firebase.repository.MessageRepository
import com.uth.synkr.model.Message

class MessageService(
    private val messageRepository: MessageRepository = MessageRepository()
) {
    suspend fun createMessage(message: Message): String {
        return messageRepository.add(message)
    }

    suspend fun updateMessage(id: String, message: Message) {
        messageRepository.update(id, message)
    }

    suspend fun deleteMessage(id: String) {
        messageRepository.delete(id)
    }

    suspend fun getMessage(id: String): Message? {
        return messageRepository.get(id)
    }

    suspend fun getAllMessages(): List<Message> {
        return messageRepository.getAll()
    }

    suspend fun getMessagesByConversationId(conversationId: String): List<Message> {
        return messageRepository.getByConversationId(conversationId)
    }
}
