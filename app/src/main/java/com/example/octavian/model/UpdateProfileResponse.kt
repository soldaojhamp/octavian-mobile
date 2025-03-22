package com.example.octavian.model

class UpdateProfileResponse (
    val success: Boolean,
    val message: String,
    val userId: Int,
    val user_id: Int? // Ensure this field is present
)