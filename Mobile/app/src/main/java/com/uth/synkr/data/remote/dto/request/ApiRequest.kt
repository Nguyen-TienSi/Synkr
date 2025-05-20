package com.uth.synkr.data.remote.dto.request

data class ApiRequest(
    val method: HttpMethod,
    val url: String,
    val body: Any? = null,
    val queryParams: Map<String, String>? = null,
    val headers: Map<String, String> = emptyMap()
)

enum class HttpMethod {
    GET, POST, PUT, PATCH, DELETE, HEAD
}
