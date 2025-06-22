package com.uth.synkr.exception

data class ApiException(
    override val message: String, override val cause: Throwable? = null
) : Exception(message, cause)
