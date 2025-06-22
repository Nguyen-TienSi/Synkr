package com.uth.synkr.data.service

import com.uth.synkr.data.model.Friendship
import com.uth.synkr.data.repository.FriendshipRepository

class FriendshipService(
    private val friendshipRepository: FriendshipRepository = FriendshipRepository()
) {
    suspend fun createFriendship(friendship: Friendship): String {
        return friendshipRepository.add(friendship)
    }

    suspend fun updateFriendship(id: String, friendship: Friendship) {
        friendshipRepository.update(id, friendship)
    }

    suspend fun deleteFriendship(id: String) {
        friendshipRepository.delete(id)
    }

    suspend fun getFriendship(id: String): Friendship? {
        return friendshipRepository.get(id)
    }

    suspend fun getAllFriendships(): List<Friendship> {
        return friendshipRepository.getAll()
    }

    suspend fun getPendingFriendshipsByRequester(userId: String): List<Friendship> {
        return friendshipRepository.getPendingByRequester(userId)
    }

    suspend fun getPendingFriendshipsByAddressee(userId: String): List<Friendship> {
        return friendshipRepository.getPendingByAddressee(userId)
    }
}
