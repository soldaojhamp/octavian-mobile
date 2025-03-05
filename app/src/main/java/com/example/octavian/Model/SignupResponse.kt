package com.example.octavian.Model

data class SignUpResponse(
    val success: Boolean,
    val message: String,
    val user: User? = null
)

data class User(
    val id: Int? = null,
    val name: String,
    val email: String,
    val password: String

)