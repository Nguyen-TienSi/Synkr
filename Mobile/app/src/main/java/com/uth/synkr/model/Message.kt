package com.uth.synkr.model

import com.uth.synkr.model.enumeration.MessageType
import java.util.Date

data class Message(
    val senderId: String,
    val receiverId: String,
    val content: String,
    val timestamp: Date,
    val type: MessageType,
    val conversationId: String
)
