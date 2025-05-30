package com.uth.synkr.model

import com.uth.synkr.model.enumeration.UserStatus
import java.util.Date

data class User(
    val id: String,
    val fullName: String,
    val pictureUrl: String,
    val email: String,
    val status: UserStatus,
    val lastSeen: Date
)
