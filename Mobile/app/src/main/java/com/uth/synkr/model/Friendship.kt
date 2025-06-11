package com.uth.synkr.model

import com.uth.synkr.model.enumeration.FriendshipRequestStatus

data class Friendship(
    val requesterId: String,
    val addresseeId: String,
    val status: FriendshipRequestStatus = FriendshipRequestStatus.PENDING,
)
