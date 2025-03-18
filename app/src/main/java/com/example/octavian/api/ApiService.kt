package com.example.octavian.Api

import com.example.octavian.dataClass.CartItem
import com.example.octavian.dataClass.Product
import com.example.octavian.model.LogoutResponse
import com.example.octavian.models.SignUpResponse
import com.example.octavian.models.User
import com.example.octavian.models.LoginResponse
import com.example.octavian.models.LoginUser
import okhttp3.ResponseBody


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("app_signup.php")
    fun signup(@Body user: User): Call<SignUpResponse>

    @Headers("Content-Type: application/json")
    @POST("app_login.php")
    fun userLogin(@Body user: LoginUser ): Call<LoginResponse>

    @GET("app_products.php")
    fun getProducts(): Call<List<Product>>

    @Headers("Cache-Control: no-cache")
    @GET("get_cart_items.php")
    fun getCartItems(@Query("user_id") userId: Int): Call<List<CartItem>>

    @POST("add_to_cart.php")
    fun addToCart(@Body cartItem: CartItem): Call<ResponseBody>


    @POST("app_logout.php")
    fun logout(): Call<LogoutResponse>

}
