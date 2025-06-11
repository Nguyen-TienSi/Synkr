package com.uth.synkr.service.data

import com.uth.synkr.data.firebase.repository.UserRepository
import com.uth.synkr.model.User

class UserService(
    private val userRepository: UserRepository = UserRepository()
) {
    suspend fun createUser(user: User): String {
        return userRepository.add(user)
    }

    suspend fun updateUser(user: User) {
        userRepository.update(user.id, user)
    }
}
