package com.uth.synkr.data.repository

import com.uth.synkr.data.model.Friendship
import com.uth.synkr.data.model.enumeration.FriendshipStatus
import com.uth.synkr.firebase.storage.FireStoreDataSource
import kotlinx.coroutines.tasks.await

class FriendshipRepository : FireStoreDataSource<Friendship>(
    collectionPath = Friendship::class.java.simpleName.lowercase(),
    itemClass = Friendship::class.java
) {
    suspend fun getByStatuses(userId: String, statuses: List<FriendshipStatus>): List<Friendship> {
        val asRequester = firestore.collection(collectionPath).whereIn("status", statuses)
            .whereEqualTo("requesterId", userId).get().await().toObjects(itemClass)

        val asAddressee = firestore.collection(collectionPath).whereIn("status", statuses)
            .whereEqualTo("addresseeId", userId).get().await().toObjects(itemClass)

        return asRequester + asAddressee
    }

    suspend fun getFriendshipStatus(userId: String, friendId: String): FriendshipStatus? {
        val friendship = getByRequesterAndAddressee(userId, friendId) ?: getByRequesterAndAddressee(
            friendId, userId
        ) ?: return null
        return friendship.status
    }

    suspend fun getByRequesterAndAddressee(requesterId: String, addresseeId: String): Friendship? {
        val snapshot = firestore.collection(collectionPath).whereEqualTo("requesterId", requesterId)
            .whereEqualTo("addresseeId", addresseeId).get().await()
        return snapshot.toObjects(itemClass).firstOrNull()
    }

    suspend fun update(friendship: Friendship) {
        val snapshot =
            firestore.collection(collectionPath).whereEqualTo("requesterId", friendship.requesterId)
                .whereEqualTo("addresseeId", friendship.addresseeId).get().await()
        val doc = snapshot.documents.firstOrNull()
        doc?.id?.let { update(it, friendship) }
    }

    suspend fun delete(friendship: Friendship) {
        val snapshot =
            firestore.collection(collectionPath).whereEqualTo("requesterId", friendship.requesterId)
                .whereEqualTo("addresseeId", friendship.addresseeId).get().await()
        val doc = snapshot.documents.firstOrNull()
        doc?.id?.let { delete(it) }
    }
}
