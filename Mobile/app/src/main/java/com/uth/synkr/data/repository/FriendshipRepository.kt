package com.uth.synkr.data.repository

import com.uth.synkr.data.model.Friendship
import com.uth.synkr.data.model.enumeration.FriendshipStatus
import com.uth.synkr.firebase.storage.FireStoreDataSource
import kotlinx.coroutines.tasks.await

class FriendshipRepository : FireStoreDataSource<Friendship>(
    collectionPath = Friendship::class.java.simpleName.lowercase(),
    itemClass = Friendship::class.java
) {
    suspend fun getPendingByRequester(userId: String): List<Friendship> {
        val snapshot = firestore.collection(collectionPath)
            .whereEqualTo("status", FriendshipStatus.PENDING)
            .whereEqualTo("requesterId", userId).get().await()
        return snapshot.toObjects(itemClass)
    }

    suspend fun getPendingByAddressee(userId: String): List<Friendship> {
        val snapshot = firestore.collection(collectionPath)
            .whereEqualTo("status", FriendshipStatus.PENDING)
            .whereEqualTo("addresseeId", userId).get().await()
        return snapshot.toObjects(itemClass)
    }
}
