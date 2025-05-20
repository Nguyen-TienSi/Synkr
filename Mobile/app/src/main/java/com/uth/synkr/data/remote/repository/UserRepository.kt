package com.uth.synkr.data.remote.repository

import com.uth.synkr.data.remote.api.ApiResult.Loading.orElseGet
import com.uth.synkr.data.remote.api.ApiResult.Loading.orElseThrow
import com.uth.synkr.data.remote.dto.request.ApiRequest
import com.uth.synkr.data.remote.dto.request.HttpMethod
import com.uth.synkr.data.remote.dto.request.JsonPatchOperation
import com.uth.synkr.data.remote.dto.request.UserCreationData
import com.uth.synkr.data.remote.dto.request.UserUpdateData
import com.uth.synkr.data.remote.dto.response.UserDetailsData
import com.uth.synkr.data.remote.dto.response.UserSummaryData
import com.uth.synkr.exception.ApiException

class UserRepository : GenericRepository() {

    private val token = "";

    suspend fun getById(id: String): UserDetailsData? =
        execute<UserDetailsData>(
            ApiRequest(
                method = HttpMethod.GET,
                url = "/api/users/$id",
                headers = mapOf("Authorization" to "Bearer $token")
            )
        ).orElseGet { null }

    suspend fun getAll(): Collection<UserSummaryData> =
        execute<Collection<UserSummaryData>>(
            ApiRequest(
                method = HttpMethod.GET,
                url = "/api/users",
                headers = mapOf("Authorization" to "Bearer $token")
            )
        ).orElseGet { emptyList() }

    suspend fun create(body: UserCreationData): UserDetailsData =
        execute<UserDetailsData>(
            ApiRequest(
                method = HttpMethod.POST,
                url = "/api/users",
                body = body,
                headers = mapOf("Authorization" to "Bearer $token")
            )
        ).orElseThrow { message, cause ->
            ApiException("Error creating user: $message", cause)
        }

    suspend fun update(id: String, body: UserUpdateData): UserDetailsData =
        execute<UserDetailsData>(
            ApiRequest(
                method = HttpMethod.PUT,
                url = "/api/users/$id",
                body = body,
                headers = mapOf("Authorization" to "Bearer $token")
            )
        ).orElseThrow { message, cause ->
            ApiException("Error updating user: $message", cause)
        }

    suspend fun delete(id: String): Unit =
        execute<Unit>(
            ApiRequest(
                method = HttpMethod.DELETE,
                url = "/api/users/$id",
                headers = mapOf("Authorization" to "Bearer $token")
            )
        ).orElseThrow { message, cause ->
            ApiException("Error deleting user: $message", cause)
        }

    suspend fun patch(id: String, operations: List<JsonPatchOperation>): UserDetailsData =
        execute<UserDetailsData>(
            ApiRequest(
                method = HttpMethod.PATCH,
                url = "/api/users/$id",
                body = operations,
                headers = mapOf("Authorization" to "Bearer $token")
            )
        ).orElseThrow { message, cause ->
            ApiException("Error patching user: $message", cause)
        }
}
