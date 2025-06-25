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
