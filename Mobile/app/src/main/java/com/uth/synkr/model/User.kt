package com.uth.synkr.model

data class User(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val isFriend: Boolean = false
)
