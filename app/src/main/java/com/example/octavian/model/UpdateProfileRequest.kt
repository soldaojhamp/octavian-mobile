package com.example.octavian.model

data class UpdateProfileRequest(
    val user_id: Int,
    val user_fullname: String,
    val user_name: String,
    val user_email: String,
    val contact_number: String,
    val city: String
)