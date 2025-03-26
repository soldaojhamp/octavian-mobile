package com.example.octavian.models

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val userId: Int,
    val user_name: String,
    val user_id: Int // Ensure this field is present

)

data class LoginUser(
    val user_email: String,
    val password: String,
)


