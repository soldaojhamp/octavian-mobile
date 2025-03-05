package com.example.octavian.Api

import com.example.octavian.Model.LoginResponse
import com.example.octavian.Model.LoginUser
import com.example.octavian.Model.SignUpResponse
import com.example.octavian.Model.User


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("http://192.168.24.15/final_admin_api/app_signup.php")
    fun signUp(@Body user: User): Call<SignUpResponse>



//
    @Headers("Content-Type: application/json") // Set the content type for the login request
    @POST("http://192.168.24.15/final_admin_api/app_login.php")
    fun userLogin(@Body user: LoginUser): Call<LoginResponse> // Accept LoginUser  object
}