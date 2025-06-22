package com.uth.synkr.data.remote.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.HeaderMap
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface GenericApiService {
    @GET
    suspend fun get(
        @Url url: String,
        @QueryMap queryParams: Map<String, String> = emptyMap(),
        @HeaderMap headers: Map<String, String> = emptyMap()
    ): Response<ResponseBody>

    @POST
    suspend fun post(
        @Url url: String, @Body body: Any, @HeaderMap headers: Map<String, String> = emptyMap()
    ): Response<ResponseBody>

    @PUT
    suspend fun put(
        @Url url: String, @Body body: Any, @HeaderMap headers: Map<String, String> = emptyMap()
    ): Response<ResponseBody>

    @PATCH
    suspend fun patch(
        @Url url: String, @Body body: Any, @HeaderMap headers: Map<String, String> = emptyMap()
    ): Response<ResponseBody>

    @DELETE
    suspend fun delete(
        @Url url: String, @HeaderMap headers: Map<String, String> = emptyMap()
    ): Response<ResponseBody>

    @HEAD
    suspend fun head(
        @Url url: String,
        @QueryMap queryParams: Map<String, String> = emptyMap(),
        @HeaderMap headers: Map<String, String> = emptyMap()
    ): Response<ResponseBody>
}
