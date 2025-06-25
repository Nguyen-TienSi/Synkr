package com.uth.synkr.data.repository

import com.uth.synkr.data.model.User
import com.uth.synkr.firebase.storage.FireStoreDataSource
import kotlinx.coroutines.tasks.await

class UserRepository : FireStoreDataSource<User>(
    collectionPath = User::class.java.simpleName.lowercase(), itemClass = User::class.java
) {
    suspend fun getByFullNameLike(fullName: String): List<User> {
        val trimmedFullName = fullName.trim()
        val snapshot = firestore.collection(collectionPath)
            .whereGreaterThanOrEqualTo("fullName", trimmedFullName)
            .whereLessThanOrEqualTo("fullName", "$trimmedFullName\uf8ff").get().await()
        println("Found documents: ${snapshot.documents.size}")
        return snapshot.toObjects(itemClass)
    }

    suspend fun getByEmail(email: String): User? {
        val trimmedEmail = email.trim()
        val snapshot =
            firestore.collection(collectionPath).whereEqualTo("email", trimmedEmail).get().await()
        println("Found documents: ${snapshot.documents.size}")
        return snapshot.documents.firstOrNull()?.toObject(itemClass)
    }

    suspend fun getByUid(uid: String): User? {
        val snapshot = firestore.collection(collectionPath).whereEqualTo("uid", uid).get().await()
        println("Found documents: ${snapshot.documents.size}")
        return snapshot.documents.firstOrNull()?.toObject(itemClass)
    }
}
