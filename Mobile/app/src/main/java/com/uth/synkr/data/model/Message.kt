package com.uth.synkr.data.model

import com.google.firebase.Timestamp
import com.uth.synkr.data.model.enumeration.MessageType

data class Message(
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val type: MessageType = MessageType.TEXT,
    val conversationId: String = ""
)
