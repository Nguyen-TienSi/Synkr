package com.uth.synkr.data.firebase.repository

import com.uth.synkr.model.Message

class MessageRepository : FireStoreRepository<Message>(
    collectionPath = Message::class.java.simpleName.lowercase().toString(),
    itemClass = Message::class.java
) {
}
