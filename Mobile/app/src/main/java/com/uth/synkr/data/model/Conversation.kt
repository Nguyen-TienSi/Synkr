package com.uth.synkr.data.model

data class Conversation(
    val name: String = "",
    val participantIds: List<String> = emptyList(),
    val messageIds: List<String> = emptyList()
)
