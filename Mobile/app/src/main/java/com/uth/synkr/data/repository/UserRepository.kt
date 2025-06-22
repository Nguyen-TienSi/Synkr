package com.uth.synkr.data.repository

import com.uth.synkr.data.model.User
import com.uth.synkr.firebase.storage.FireStoreDataSource
import kotlinx.coroutines.tasks.await

class UserRepository : FireStoreDataSource<User>(
    collectionPath = User::class.java.simpleName.lowercase().toString(),
    itemClass = User::class.java
) {
    suspend fun getByEmail(email: String): User? {
        val trimmedEmail = email.trim()
        val snapshot = firestore.collection(collectionPath)
            .whereEqualTo("email", trimmedEmail)
            .get().await()
        println("Found documents: ${snapshot.documents.size}")
        return snapshot.documents.firstOrNull()?.toObject(itemClass)
    }

    suspend fun setUserWithUid(uid: String, user: User) {
        setWithId(uid, user)
    }
}
