package com.example.octavian.model

data class UserProfileResponse(
    val success: Boolean,
    val message: String?,// Optional message field

    val user_fullname: String,
    val user_name : String,
    val user_email: String,
    val contact_number: String,
    val city: String,
    val user_profile_url: String?,
    var isSelected: Boolean = false
)