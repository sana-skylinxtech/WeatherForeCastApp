package com.example.weatherforecastapp.Retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitHelper {

    private const val BASE_URL = "https://api.openweathermap.org/data/3.0/"
    private val client = OkHttpClient.Builder().connectTimeout(40, TimeUnit.SECONDS)
        .writeTimeout(40, TimeUnit.SECONDS)
        .readTimeout(50, TimeUnit.SECONDS).build()

    private val retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    fun<T> createService(bindService : Class<T>):T{
        return retrofit.create(bindService)
    }
}