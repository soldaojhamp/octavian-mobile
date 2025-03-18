package com.example.octavian.models

data class SignUpResponse(
    val success: Boolean,
    val message: String,
    val userId: User? = null
)

data class User(
    val user_id: Int? = null,
    val name: String,
    val email: String,
    val password: String
)