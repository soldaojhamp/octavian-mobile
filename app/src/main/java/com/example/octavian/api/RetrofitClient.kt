// RetrofitClient.kt
package com.example.octavian.Api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.123.70/octavian_web/APP_DB/" // Replace with your actual base URL

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

    // Create a lenient Gson instance
    private val gson = GsonBuilder()
        .setLenient() // This will help with malformed JSON
        .create()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson)) // Use the lenient Gson
            .build()
    }

    val instance: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}