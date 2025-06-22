package com.uth.synkr.data.repository

import com.uth.synkr.data.model.Conversation
import com.uth.synkr.firebase.storage.FireStoreDataSource
import kotlinx.coroutines.tasks.await

class ConversationRepository : FireStoreDataSource<Conversation>(
    collectionPath = Conversation::class.java.simpleName.lowercase().toString(),
    itemClass = Conversation::class.java
) {
    suspend fun getByParticipant(userId: String): List<Conversation> {
        val snapshot = firestore.collection(collectionPath)
            .whereArrayContains("participantIds", userId)
            .get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(itemClass)?.apply { id = doc.id }
        }
    }

    override suspend fun get(id: String): Conversation? {
        val doc = firestore.collection(collectionPath).document(id).get().await()
        return doc.toObject(itemClass)?.apply { this.id = doc.id }
    }
}
