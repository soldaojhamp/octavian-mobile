package com.example.octavian.model
data class SignUpResponse(
    val success: Boolean,
    val message: String,
    val user: Int? = null
)

data class SignUpUser (
    val email: String,
    val username: String,
    val password: String
)

