package com.uth.synkr.data.repository

import android.util.Log
import com.uth.synkr.data.model.Friendship
import com.uth.synkr.data.model.enumeration.FriendshipStatus
import com.uth.synkr.firebase.storage.FireStoreDataSource
import kotlinx.coroutines.tasks.await

class FriendshipRepository : FireStoreDataSource<Friendship>(
    collectionPath = Friendship::class.java.simpleName.lowercase(),
    itemClass = Friendship::class.java
) {
    suspend fun getByStatuses(userId: String, statuses: List<FriendshipStatus>): List<Friendship> {
        return try {
            val asRequester = firestore.collection(collectionPath).whereIn("status", statuses)
                .whereEqualTo("requesterId", userId).get().await().toObjects(itemClass)

            val asAddressee = firestore.collection(collectionPath).whereIn("status", statuses)
                .whereEqualTo("addresseeId", userId).get().await().toObjects(itemClass)

            Log.d(TAG, "getByStatuses: Found ${asRequester.size + asAddressee.size} friendships")
            asRequester + asAddressee
        } catch (e: Exception) {
            Log.e(TAG, "Error in getByStatuses: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getFriendshipStatus(userId: String, friendId: String): FriendshipStatus? {
        return try {
            val friendship = getByRequesterAndAddressee(userId, friendId) ?: getByRequesterAndAddressee(
                friendId, userId
            ) ?: return null
            Log.d(TAG, "getFriendshipStatus: Found status ${friendship.status}")
            friendship.status
        } catch (e: Exception) {
            Log.e(TAG, "Error in getFriendshipStatus: ${e.message}", e)
            null
        }
    }

    suspend fun getByRequesterAndAddressee(requesterId: String, addresseeId: String): Friendship? {
        return try {
            val snapshot = firestore.collection(collectionPath).whereEqualTo("requesterId", requesterId)
                .whereEqualTo("addresseeId", addresseeId).get().await()
            Log.d(TAG, "getByRequesterAndAddressee: Found documents: ${snapshot.documents.size}")
            snapshot.toObjects(itemClass).firstOrNull()
        } catch (e: Exception) {
            Log.e(TAG, "Error in getByRequesterAndAddressee: ${e.message}", e)
            null
        }
    }

    suspend fun update(friendship: Friendship) {
        try {
            val snapshot =
                firestore.collection(collectionPath).whereEqualTo("requesterId", friendship.requesterId)
                    .whereEqualTo("addresseeId", friendship.addresseeId).get().await()
            val doc = snapshot.documents.firstOrNull()
            doc?.id?.let { 
                update(it, friendship)
                Log.d(TAG, "update: Successfully updated friendship with ID: ${doc.id}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in update: ${e.message}", e)
        }
    }

    suspend fun delete(friendship: Friendship) {
        try {
            val snapshot =
                firestore.collection(collectionPath).whereEqualTo("requesterId", friendship.requesterId)
                    .whereEqualTo("addresseeId", friendship.addresseeId).get().await()
            val doc = snapshot.documents.firstOrNull()
            doc?.id?.let { 
                delete(it)
                Log.d(TAG, "delete: Successfully deleted friendship with ID: ${doc.id}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in delete: ${e.message}", e)
        }
    }

    companion object {
        private const val TAG = "FriendshipRepository"
    }
}
