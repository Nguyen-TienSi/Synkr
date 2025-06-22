package com.uth.synkr.data.repository

import com.uth.synkr.data.model.Conversation
import com.uth.synkr.firebase.storage.FireStoreDataSource
import kotlinx.coroutines.tasks.await

class ConversationRepository : FireStoreDataSource<Conversation>(
    collectionPath = Conversation::class.java.simpleName.lowercase().toString(),
    itemClass = Conversation::class.java
) {
    suspend fun getByParticipant(userId: String): List<Conversation> {
        val snapshot =
            firestore.collection(collectionPath).whereArrayContains("participants", userId).get()
                .await()
        return snapshot.toObjects(itemClass)
    }
}
