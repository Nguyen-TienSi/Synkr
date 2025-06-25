package com.uth.synkr.data.repository

import com.uth.synkr.data.model.Message
import com.uth.synkr.firebase.storage.FireStoreDataSource

class MessageRepository : FireStoreDataSource<Message>(
    collectionPath = Message::class.java.simpleName.lowercase(),
    itemClass = Message::class.java
)
