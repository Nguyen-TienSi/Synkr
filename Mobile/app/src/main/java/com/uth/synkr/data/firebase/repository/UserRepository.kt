package com.uth.synkr.data.firebase.repository

import com.uth.synkr.model.User

class UserRepository : FireStoreRepository<User>(
    collectionPath = User::class.java.simpleName.lowercase().toString(),
    itemClass = User::class.java
)
