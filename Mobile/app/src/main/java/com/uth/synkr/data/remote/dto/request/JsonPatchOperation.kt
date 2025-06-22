package com.uth.synkr.data.remote.dto.request

data class JsonPatchOperation(
    val op: String, val path: String, val value: Any?
)
