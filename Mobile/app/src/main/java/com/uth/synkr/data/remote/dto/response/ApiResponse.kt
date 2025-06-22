package com.uth.synkr.data.remote.dto.response

data class ApiResponse<T>(
    val data: T?, val httpStatus: Int, val error: String?
)
