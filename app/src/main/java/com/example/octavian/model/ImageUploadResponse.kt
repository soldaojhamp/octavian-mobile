package com.example.octavian.model

data class ImageUploadResponse(
    val success: Boolean,
    val url: String? = null,
    val message: String? = null
)