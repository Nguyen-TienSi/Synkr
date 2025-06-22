package com.uth.synkr.data.service

import com.uth.synkr.data.model.User
import com.uth.synkr.data.model.enumeration.FriendshipStatus
import com.uth.synkr.data.repository.FriendshipRepository
import com.uth.synkr.data.repository.UserRepository

class UserService(
    private val userRepository: UserRepository = UserRepository(),
    private val friendshipRepository: FriendshipRepository = FriendshipRepository(),
) {
    suspend fun createUser(user: User): String {
        userRepository.setUserDocWithUid(user.uid, user)
        return user.uid
    }

    suspend fun getByFullNameLike(fullName: String): List<User> {
        return userRepository.getByFullNameLike(fullName)
    }

    suspend fun getByEmail(email: String): User? {
        return userRepository.getByEmail(email)
    }

    suspend fun searchUsers(query: String, currentUserId: String): List<User> {
        val byEmail = getByEmail(query)
        return if (byEmail != null) {
            if (byEmail.uid != currentUserId) listOf(byEmail) else emptyList()
        } else {
            getByFullNameLike(query).filter { it.uid != currentUserId }
        }
    }

    suspend fun getFriendsOfUser(currentUserId: String): List<User> {
        val friendships = friendshipRepository.getAllFriendshipsOfUserWithStatuses(
            currentUserId, listOf(FriendshipStatus.ACCEPTED, FriendshipStatus.PENDING)
        )
        val friendIds = friendships.mapNotNull {
            when {
                it.requesterId == currentUserId -> it.addresseeId
                it.addresseeId == currentUserId -> it.requesterId
                else -> null
            }
        }.distinct()

        return friendIds.mapNotNull { userRepository.getById(it) }
    }
}
