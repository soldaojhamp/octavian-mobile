package com.example.octavian.models

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val user: Int? = null

)

data class LoginUser(
    val email: String,
    val password: String
)


