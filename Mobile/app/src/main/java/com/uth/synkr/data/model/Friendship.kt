package com.uth.synkr.data.model

import com.uth.synkr.data.model.enumeration.FriendshipStatus

data class Friendship(
    val requesterId: String = "",
    val addresseeId: String = "",
    val status: FriendshipStatus = FriendshipStatus.PENDING,
)
