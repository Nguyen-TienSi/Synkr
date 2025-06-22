package com.uth.synkr.data.repository

import com.uth.synkr.data.model.Friendship
import com.uth.synkr.data.model.enumeration.FriendshipStatus
import com.uth.synkr.firebase.storage.FireStoreDataSource
import kotlinx.coroutines.tasks.await

class FriendshipRepository : FireStoreDataSource<Friendship>(
    collectionPath = Friendship::class.java.simpleName.lowercase(),
    itemClass = Friendship::class.java
) {
    suspend fun setFriendshipDocWithId(id: String, friendship: Friendship) {
        setWithId(id, friendship)
    }

    suspend fun getAllFriendshipsOfUserWithStatuses(
        userId: String,
        statuses: List<FriendshipStatus>
    ): List<Friendship> {
        val asRequester = firestore.collection(collectionPath).whereIn("status", statuses)
            .whereEqualTo("requesterId", userId).get().await().toObjects(itemClass)

        val asAddressee = firestore.collection(collectionPath).whereIn("status", statuses)
            .whereEqualTo("addresseeId", userId).get().await().toObjects(itemClass)

        return asRequester + asAddressee
    }

    suspend fun getFriendshipStatus(userId: String, friendId: String): FriendshipStatus? {
        val docId1 = userId + "_" + friendId
        val docId2 = friendId + "_" + userId
        val friendship = get(docId1) ?: get(docId2) ?: return null
        return friendship.status
    }
}
