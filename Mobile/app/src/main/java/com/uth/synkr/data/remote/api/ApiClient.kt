package com.uth.synkr.data.remote.api

import com.uth.synkr.data.remote.adapter.JsonAdapterFactory
import com.uth.synkr.data.remote.service.GenericApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = ""

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
            val request =
                chain.request().newBuilder().addHeader("Authorization", "Bearer TOKEN").build()
            chain.proceed(request)
        }.connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS).build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(JsonAdapterFactory.moshi))
            .client(okHttpClient).build()
    }

    val genericApiService: GenericApiService by lazy {
        retrofit.create(GenericApiService::class.java)
    }
}
