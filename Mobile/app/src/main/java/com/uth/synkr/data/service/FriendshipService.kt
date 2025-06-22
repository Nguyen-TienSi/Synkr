package com.uth.synkr.data.service

import com.uth.synkr.data.model.Friendship
import com.uth.synkr.data.model.enumeration.FriendshipStatus
import com.uth.synkr.data.repository.FriendshipRepository

class FriendshipService(
    private val friendshipRepository: FriendshipRepository = FriendshipRepository()
) {
    suspend fun createFriendship(friendship: Friendship): String {
        val docId = friendship.requesterId + "_" + friendship.addresseeId
        friendshipRepository.setFriendshipDocWithId(docId, friendship)
        return docId
    }

    suspend fun acceptRequest(userId: String, requesterId: String) {
        val docId = requesterId + "_" + userId
        val friendship = friendshipRepository.get(docId)
        if (friendship != null) {
            friendship.status = FriendshipStatus.ACCEPTED
            friendshipRepository.update(docId, friendship)
        }
    }

    suspend fun rejectRequest(userId: String, requesterId: String) {
        val docId = requesterId + "_" + userId
        friendshipRepository.delete(docId)
    }

    suspend fun cancelRequest(userId: String, requesterId: String) {
        val docId = requesterId + "_" + userId
        friendshipRepository.delete(docId)
    }

    suspend fun unfriend(userId: String, friendId: String) {
        val docId1 = userId + "_" + friendId
        val docId2 = friendId + "_" + userId
        if (friendshipRepository.get(docId1) != null) {
            friendshipRepository.delete(docId1)
        } else if (friendshipRepository.get(docId2) != null) {
            friendshipRepository.delete(docId2)
        }
    }

    suspend fun getFriendshipStatus(userId: String, friendId: String): FriendshipStatus? {
        return friendshipRepository.getFriendshipStatus(userId, friendId)
    }

    suspend fun getFriendship(userId: String, friendId: String): Friendship? {
        val docId1 = userId + "_" + friendId
        val docId2 = friendId + "_" + userId
        return friendshipRepository.get(docId1) ?: friendshipRepository.get(docId2)
    }
}
