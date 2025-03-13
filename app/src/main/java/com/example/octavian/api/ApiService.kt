package com.example.octavian.Api

import com.example.octavian.models.SignUpResponse
import com.example.octavian.models.User
import com.example.octavian.models.LoginResponse
import com.example.octavian.models.LoginUser


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("http://192.168.212.15/final_admin_api/APP_DB/app_signup.php")
    fun signup(@Body user: User): Call<SignUpResponse>



    //
    @Headers("Content-Type: application/json") // Set the content type for the login request
    @POST("http://192.168.212.15/final_admin_api/APP_DB/app_login.php")
    fun userLogin(@Body user: LoginUser): Call<LoginResponse> // Accept LoginUser  object

}