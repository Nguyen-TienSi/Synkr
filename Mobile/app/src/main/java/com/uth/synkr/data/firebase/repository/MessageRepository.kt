package com.uth.synkr.data.firebase.repository

import com.uth.synkr.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MessageRepository : FireStoreRepository<Message>(
    collectionPath = Message::class.java.simpleName.lowercase().toString(),
    itemClass = Message::class.java
) {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getByConversationId(conversationId: String): List<Message> {
        val snapshot = firestore.collection(collectionPath)
            .whereEqualTo("conversationId", conversationId)
            .get()
            .await()
        return snapshot.toObjects(itemClass)
    }
}
