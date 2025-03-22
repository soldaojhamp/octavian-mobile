package com.example.octavian.Api

import com.example.octavian.model.UpdateProfileResponse
import com.example.octavian.model.UserProfileResponse
import com.example.octavian.dataClass.CartItem
import com.example.octavian.dataClass.Product
import com.example.octavian.model.LogoutResponse
import com.example.octavian.models.SignUpResponse
import com.example.octavian.models.User
import com.example.octavian.models.LoginResponse
import com.example.octavian.models.LoginUser
import okhttp3.ResponseBody


import retrofit2.Call
import retrofit2.Response
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
    suspend fun signup(@Body user: User): Response<SignUpResponse>

    @Headers("Content-Type: application/json")
    @POST("app_login.php")
    suspend fun userLogin(@Body user: LoginUser): Response<LoginResponse>

    @GET("app_products.php")
    suspend fun getProducts(): Response<List<Product>>

    @Headers("Cache-Control: no-cache")
    @GET("get_cart_items.php")
    suspend fun getCartItems(@Query("user_id") userId: Int): Response<List<CartItem>>

    @POST("add_to_cart.php")
    suspend fun addToCart(@Body cartItem: CartItem): Response<ResponseBody>

    @POST("app_logout.php")
    suspend fun logout(): Response<LogoutResponse>

    // EDIT PROFILE
    @GET("get_userprofile.php")
    suspend fun getUserProfile(@Query("user_id") userId: Int): Response<UserProfileResponse>

    @FormUrlEncoded
    @POST("update_userprofile.php")
    suspend fun updateUserProfile(
        @Field("user_id") userId: Int,
        @Field("user_fullname") fullName: String,
        @Field("user_name") userName: String,
        @Field("user_email") email: String,
        @Field("contact_number") contactNumber: String,
        @Field("address") address: String
    ): Response<UpdateProfileResponse>
}