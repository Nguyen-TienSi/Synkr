package com.uth.synkr.data.remote.api

sealed class ApiResult<out T> {
    data object Loading : ApiResult<Nothing>()
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : ApiResult<Nothing>()

    fun <I, O> ApiResult<I>.map(transform: (I) -> O): ApiResult<O> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(message, cause)
        Loading -> throw NotImplementedError("An operation is not implemented.")
    }

    fun <T> ApiResult<T>.orElseThrow(exception: (String, Throwable?) -> Throwable): T = when (this) {
        is Success -> this.data
        is Error -> throw exception(message, cause)
        Loading -> throw NotImplementedError("An operation is not implemented.")
    }

    fun <T> ApiResult<T>.orElseGet(fallback: () -> T): T = when (this) {
        is Success -> this.data
        else -> fallback()
    }

    inline fun <T> ApiResult<T>.onSuccess(block: (T) -> Unit): ApiResult<T> {
        if (this is Success) block(data)
        return this
    }

    inline fun <T> ApiResult<T>.onError(block: (String, Throwable?) -> Unit): ApiResult<T> {
        if (this is Error) block(message, cause)
        return this
    }

    inline fun <T> ApiResult<T>.onLoading(block: () -> Unit): ApiResult<T> {
        if (this is Loading) block()
        return this
    }
}
