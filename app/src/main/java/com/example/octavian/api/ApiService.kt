package com.example.octavian.Api

import com.example.octavian.model.UserProfileResponse
import com.example.octavian.dataClass.CartItem
import com.example.octavian.dataClass.Product
import com.example.octavian.model.LogoutResponse
import com.example.octavian.model.UpdateProfileRequest
import com.example.octavian.model.UpdateProfileResponse
import com.example.octavian.models.SignUpResponse
import com.example.octavian.models.User
import com.example.octavian.models.LoginResponse
import com.example.octavian.models.LoginUser
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("deleteCartItems.php")
    suspend fun deleteCartItems(
        @Field("user_id") userId: Int,
        @Field("product_ids") productIds: String
    ): Response<Map<String, Any>>

    @GET("app_order.php")
    suspend fun getOrderItems(
        @Query("user_id") userId: Int
    ): Response<List<CartItem.OrderItem>>

    @GET("app_order.php")
    suspend fun getOrderItemsByStatus(
        @Query("user_id") userId: Int,
        @Query("status") status: String
    ): Response<List<CartItem.OrderItem>>

    @POST("checkout.php")
    @Headers("Content-Type: application/json")
    suspend fun placeOrder(@Body orderData: RequestBody): Response<ResponseBody>


    @Headers("Content-Type: application/json")
    @POST("app_signup.php")
    suspend fun signup(@Body user: User): Response<SignUpResponse>

    @Headers("Content-Type: application/json")
    @POST("app_login.php")
    suspend fun userLogin(@Body user: LoginUser): Response<LoginResponse>

    @GET("app_products.php")
    suspend fun getProducts(): Response<List<Product>>

    @GET("app_products.php")
    suspend fun getProductsByCategory(@Query("category") category: String): Response<List<Product>>

    @Headers("Cache-Control: no-cache")
    @GET("get_cart_items.php")
    suspend fun getCartItems(@Query("user_id") userId: Int): Response<List<CartItem>>

    @POST("add_to_cart.php")
    suspend fun addToCart(@Body cartItem: CartItem): Response<ResponseBody>

    @POST("app_logout.php")
    suspend fun logout(): Response<LogoutResponse>

    // EDIT PROFILE
    @GET("get_userprofile.php")
    suspend fun getUserProfile(
        @Query("user_id") userId: Int // Use @Query instead of @Path
    ): Response<UserProfileResponse>

    @Headers("Content-Type: application/json")
    @POST("update_userprofile.php")
    suspend fun updateUserProfile(
        @Body request: UpdateProfileRequest // Accept the request object
    ): Response<UpdateProfileResponse>

    @Multipart
    @POST("update_userprofile.php")
    suspend fun uploadImage(@Part file: MultipartBody.Part): Response<ImageUploadResponse>
}



data class ImageUploadResponse(
    val success: Boolean,
    val url: String? = null,
    val message: String? = null
)