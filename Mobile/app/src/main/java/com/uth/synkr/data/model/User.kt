package com.uth.synkr.data.model

import com.uth.synkr.data.model.enumeration.UserStatus
import com.google.firebase.Timestamp

data class User(
    val uid: String = "",
    val fullName: String = "",
    val pictureUrl: String = "",
    val email: String = "",
    val status: UserStatus = UserStatus.ONLINE,
    val lastSeen: Timestamp? = null
)
