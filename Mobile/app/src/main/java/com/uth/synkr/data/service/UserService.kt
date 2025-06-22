package com.uth.synkr.data.service

import com.uth.synkr.data.model.User
import com.uth.synkr.data.repository.UserRepository

class UserService(
    private val userRepository: UserRepository = UserRepository()
) {
    suspend fun createUser(user: User): String {
        userRepository.setUserWithUid(user.uid, user)
        return user.uid
    }

    suspend fun getByEmail(email: String): User? {
        return userRepository.getByEmail(email)
    }
}
