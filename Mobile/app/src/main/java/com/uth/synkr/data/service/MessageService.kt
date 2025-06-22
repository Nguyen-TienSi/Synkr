package com.uth.synkr.data.service

import com.uth.synkr.data.model.Conversation
import com.uth.synkr.data.model.Message
import com.uth.synkr.data.repository.MessageRepository

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

    suspend fun getByConversation(conversation: Conversation): List<Message> {

        val messages = mutableListOf<Message>()

        for (messageId in conversation.messageIds) {
            val message = messageRepository.get(messageId)
            if (message != null) {
                messages.add(message)
            }
        }

        return messages
    }
}
