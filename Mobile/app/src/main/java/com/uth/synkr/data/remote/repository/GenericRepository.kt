package com.uth.synkr.data.remote.repository

import android.util.Log
import com.uth.synkr.data.remote.adapter.JsonAdapterFactory
import com.uth.synkr.data.remote.api.ApiClient
import com.uth.synkr.data.remote.api.ApiResult
import com.uth.synkr.data.remote.dto.request.ApiRequest
import com.uth.synkr.data.remote.dto.request.HttpMethod
import com.uth.synkr.data.remote.dto.response.ApiResponse
import com.uth.synkr.data.remote.service.GenericApiService
import com.uth.synkr.exception.ErrorType
import com.uth.synkr.exception.RemoteErrorEmitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

open class GenericRepository(
    val api: GenericApiService = ApiClient.genericApiService
) {

    suspend inline fun <reified T> execute(request: ApiRequest): ApiResult<T> {
        return try {
            val response = when (request.method) {
                HttpMethod.GET -> api.get(
                    request.url,
                    request.queryParams ?: emptyMap(),
                    request.headers
                )

                HttpMethod.POST -> api.post(request.url, request.body ?: Unit, request.headers)
                HttpMethod.PUT -> api.put(request.url, request.body ?: Unit, request.headers)
                HttpMethod.PATCH -> api.patch(request.url, request.body ?: Unit, request.headers)
                HttpMethod.DELETE -> api.delete(request.url, request.headers)
                HttpMethod.HEAD -> api.head(
                    request.url,
                    request.queryParams ?: emptyMap(),
                    request.headers
                )
            }

            if (response.isSuccessful) {
                val apiResponse = JsonAdapterFactory.moshi.adapter(ApiResponse::class.java)
                    .fromJson(response.body()?.string() ?: "")
                ApiResult.Success(apiResponse?.data as T)
            } else {
                val error = response.errorBody()?.string() ?: response.message()
                ApiResult.Error("HTTP ${response.code()}: $error")
            }

        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error", e)
        }
    }

    companion object {
        private const val MESSAGE_KEY = "message"
        private const val ERROR_KEY = "error"
    }

    fun getErrorMessage(responseBody: ResponseBody?): String {
        return try {
            val jsonObject = JSONObject(responseBody!!.string())
            when {
                jsonObject.has(MESSAGE_KEY) -> jsonObject.getString(MESSAGE_KEY)
                jsonObject.has(ERROR_KEY) -> jsonObject.getString(ERROR_KEY)
                else -> "Something wrong happened"
            }
        } catch (e: Exception) {
            "Something wrong happened"
        }
    }

    suspend inline fun <T> safeApiCall(
        emitter: RemoteErrorEmitter,
        crossinline responseFunction: suspend () -> T
    ): T? {
        return try {
            val response = withContext(Dispatchers.IO) { responseFunction.invoke() }
            response
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
                Log.e("ApiCalls", "Call error: ${e.localizedMessage}", e.cause)
                when (e) {
                    is HttpException -> {
                        val body = e.response()?.errorBody()
                        emitter.onError(getErrorMessage(body))
                    }

                    is SocketTimeoutException -> emitter.onError(ErrorType.TIMEOUT)
                    is IOException -> emitter.onError(ErrorType.NETWORK)
                    else -> emitter.onError(ErrorType.UNKNOWN)
                }
            }
            null
        }
    }
}
