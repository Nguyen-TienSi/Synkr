package com.uth.synkr.data.repository

import android.util.Log
import com.uth.synkr.data.model.User
import com.uth.synkr.firebase.storage.FireStoreDataSource
import kotlinx.coroutines.tasks.await

class UserRepository : FireStoreDataSource<User>(
    collectionPath = User::class.java.simpleName.lowercase(), itemClass = User::class.java
) {
    suspend fun getByFullNameLike(fullName: String): List<User> {
        return try {
            val trimmedFullName = fullName.trim()
            val snapshot = firestore.collection(collectionPath)
                .whereGreaterThanOrEqualTo("fullName", trimmedFullName)
                .whereLessThanOrEqualTo("fullName", "$trimmedFullName\uf8ff").get().await()
            Log.d(TAG, "getByFullNameLike: Found documents: ${snapshot.documents.size}")
            snapshot.toObjects(itemClass)
        } catch (e: Exception) {
            Log.e(TAG, "Error in getByFullNameLike: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getByEmail(email: String): User? {
        return try {
            val trimmedEmail = email.trim()
            val snapshot =
                firestore.collection(collectionPath).whereEqualTo("email", trimmedEmail).get().await()
            Log.d(TAG, "getByEmail: Found documents: ${snapshot.documents.size}")
            snapshot.documents.firstOrNull()?.toObject(itemClass)
        } catch (e: Exception) {
            Log.e(TAG, "Error in getByEmail: ${e.message}", e)
            null
        }
    }

    suspend fun getByUid(uid: String): User? {
        return try {
            val snapshot = firestore.collection(collectionPath).whereEqualTo("uid", uid).get().await()
            Log.d(TAG, "getByUid: Found documents: ${snapshot.documents.size}")
            snapshot.documents.firstOrNull()?.toObject(itemClass)
        } catch (e: Exception) {
            Log.e(TAG, "Error in getByUid: ${e.message}", e)
            null
        }
    }

    companion object {
        private const val TAG = "UserRepository"
    }
}
