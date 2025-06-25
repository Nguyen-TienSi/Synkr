package com.uth.synkr.data.service

import com.uth.synkr.data.model.Friendship
import com.uth.synkr.data.model.enumeration.FriendshipStatus
import com.uth.synkr.data.repository.FriendshipRepository

class FriendshipService(
    private val friendshipRepository: FriendshipRepository = FriendshipRepository()
) {
    suspend fun getFriendship(userId: String, friendId: String): Friendship? {
        return friendshipRepository.getByRequesterAndAddressee(userId, friendId)
            ?: friendshipRepository.getByRequesterAndAddressee(friendId, userId)
    }

    suspend fun getFriendshipStatus(userId: String, friendId: String): FriendshipStatus? {
        return friendshipRepository.getFriendshipStatus(userId, friendId)
    }

    suspend fun createFriendship(friendship: Friendship): String {
        return friendshipRepository.add(friendship)
    }

    suspend fun acceptRequest(userId: String, requesterId: String) {
        val friendship = Friendship(
            requesterId = requesterId, addresseeId = userId, status = FriendshipStatus.ACCEPTED
        )
        friendshipRepository.update(friendship)
    }

    suspend fun rejectRequest(userId: String, requesterId: String) {
        val friendship = Friendship(
            requesterId = requesterId, addresseeId = userId, status = FriendshipStatus.PENDING
        )
        friendshipRepository.delete(friendship)
    }

    suspend fun cancelRequest(userId: String, requesterId: String) {
        val friendship = Friendship(
            requesterId = userId, addresseeId = requesterId, status = FriendshipStatus.PENDING
        )
        friendshipRepository.delete(friendship)
    }

    suspend fun unfriend(userId: String, friendId: String) {
        val friendship = friendshipRepository.getByRequesterAndAddressee(userId, friendId)
            ?: friendshipRepository.getByRequesterAndAddressee(friendId, userId)
        friendship ?: return
        friendshipRepository.delete(friendship)
    }
}
