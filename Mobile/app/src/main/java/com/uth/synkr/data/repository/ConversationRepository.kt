package com.uth.synkr.data.repository

import android.util.Log
import com.uth.synkr.data.model.Conversation
import com.uth.synkr.firebase.storage.FireStoreDataSource
import kotlinx.coroutines.tasks.await

class ConversationRepository : FireStoreDataSource<Conversation>(
    collectionPath = Conversation::class.java.simpleName.lowercase(),
    itemClass = Conversation::class.java
) {
    suspend fun getByParticipant(userId: String): List<Conversation> {
        return try {
            val snapshot = firestore.collection(collectionPath)
                .whereArrayContains("participantIds", userId)
                .get().await()
            Log.d(TAG, "getByParticipant: Found documents: ${snapshot.documents.size}")
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(itemClass)?.apply { id = doc.id }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in getByParticipant: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun get(docId: String): Conversation? {
        return try {
            val doc = firestore.collection(collectionPath).document(docId).get().await()
            Log.d(TAG, "get: Retrieved document with ID: $docId")
            doc.toObject(itemClass)?.apply { this.id = doc.id }
        } catch (e: Exception) {
            Log.e(TAG, "Error in get: ${e.message}", e)
            null
        }
    }

    suspend fun findPrivateConversationByParticipants(userId1: String, userId2: String): Conversation? {
        return try {
            val sortedParticipants = listOf(userId1, userId2).sorted()
            val snapshot = firestore.collection(collectionPath)
                .whereEqualTo("conversationType", "PRIVATE")
                .whereArrayContains("participantIds", userId1)
                .get().await()

            val conversations = snapshot.documents.mapNotNull { doc ->
                doc.toObject(itemClass)?.apply { id = doc.id }
            }

            val matchingConversation = conversations.firstOrNull { conversation ->
                conversation.participantIds.sorted() == sortedParticipants
            }

            Log.d(TAG, "findPrivateConversationByParticipants: Found conversation: ${matchingConversation?.id}")
            matchingConversation
        } catch (e: Exception) {
            Log.e(TAG, "Error in findPrivateConversationByParticipants: ${e.message}", e)
            null
        }
    }

    companion object {
        private const val TAG = "ConversationRepository"
    }
}
