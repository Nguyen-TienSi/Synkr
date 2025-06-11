package com.uth.synkr.data.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.uth.synkr.model.Friendship
import com.uth.synkr.model.enumeration.FriendshipRequestStatus
import kotlinx.coroutines.tasks.await

class FriendshipRepository : FireStoreRepository<Friendship>(
    collectionPath = Friendship::class.java.simpleName.lowercase(),
    itemClass = Friendship::class.java
) {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getPendingByRequester(userId: String): List<Friendship> {
        val snapshot = firestore.collection(collectionPath)
            .whereEqualTo("status", FriendshipRequestStatus.PENDING)
            .whereEqualTo("requesterId", userId)
            .get()
            .await()
        return snapshot.toObjects(itemClass)
    }

    suspend fun getPendingByAddressee(userId: String): List<Friendship> {
        val snapshot = firestore.collection(collectionPath)
            .whereEqualTo("status", FriendshipRequestStatus.PENDING)
            .whereEqualTo("addresseeId", userId)
            .get()
            .await()
        return snapshot.toObjects(itemClass)
    }
}
