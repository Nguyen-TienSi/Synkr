package com.uth.synkr.model

data class Conversation(
    val name: String,
    val participants: List<String>,
    val messages: List<String>
)
