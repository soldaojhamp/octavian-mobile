// RetrofitClient.kt
package com.example.octavian.Api

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.35.15/octavian_web/APP_DB/" // Replace with your actual base URL

    private val logging = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val token = "AuthManager" // Retrieve your token from SharedPreferences or another source
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token") // Add the token to the request header
                .build()
            chain.proceed(newRequest)
        }
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val instance: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}