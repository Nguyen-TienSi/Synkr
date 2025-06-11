package com.uth.synkr.data.firebase.repository

import com.uth.synkr.model.Conversation
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ConversationRepository : FireStoreRepository<Conversation>(
    collectionPath = Conversation::class.java.simpleName.lowercase().toString(),
    itemClass = Conversation::class.java
) {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getByParticipant(userId: String): List<Conversation> {
        val snapshot = firestore.collection(collectionPath)
            .whereArrayContains("participants", userId)
            .get()
            .await()
        return snapshot.toObjects(itemClass)
    }
}
