package com.uth.synkr.data.firebase.repository

import com.uth.synkr.model.Conversation

class ConversationRepository : FireStoreRepository<Conversation>(
    collectionPath = Conversation::class.java.simpleName.lowercase().toString(),
    itemClass = Conversation::class.java
) {
}
