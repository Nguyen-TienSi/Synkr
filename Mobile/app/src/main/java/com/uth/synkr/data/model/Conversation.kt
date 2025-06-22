package com.uth.synkr.data.model

import com.google.firebase.firestore.Exclude

data class Conversation(
    @get:Exclude var id: String? = null, // Not stored in Firestore
    val name: String = "",
    val participantIds: List<String> = emptyList(),
    val messageIds: List<String> = emptyList()
)
