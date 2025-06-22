package com.uth.synkr.firebase.storage

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

open class FireStoreDataSource<T : Any>(
    protected val collectionPath: String, protected val itemClass: Class<T>
) {
    protected val firestore: FirebaseFirestore = Firebase.firestore

    suspend fun add(item: T): String {
        val docRef = firestore.collection(collectionPath).add(item).await()
        return docRef.id
    }

    suspend fun update(docId: String, item: T) {
        firestore.collection(collectionPath).document(docId).set(item).await()
    }

    suspend fun delete(docId: String) {
        firestore.collection(collectionPath).document(docId).delete().await()
    }

    suspend fun get(docId: String): T? {
        val document = firestore.collection(collectionPath).document(docId).get().await()
        return document.toObject(itemClass)
    }

    suspend fun getAll(): List<T> {
        val snapshot = firestore.collection(collectionPath).get().await()
        return snapshot.toObjects(itemClass)
    }

    suspend fun setWithId(docId: String, item: T) {
        firestore.collection(collectionPath).document(docId).set(item).await()
    }
}
