package com.uth.synkr.data.remote.adapter

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object JsonAdapterFactory {
    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    val gson: Gson = GsonBuilder().setStrictness(Strictness.STRICT).setPrettyPrinting().create()
}