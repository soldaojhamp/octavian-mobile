package com.example.octavian.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.octavian.R
import com.example.octavian.databinding.ActivityEditProfilePageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.widget.Toast
import com.example.octavian.Api.ApiService
import com.example.octavian.model.UpdateProfileRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class EditProfilePage : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfilePageBinding
    private var userId: Int = -1
    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.35.15/octavian_web/APP_DB/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("EditProfilePage", "Activity created")

        // Get user ID from both Intent and SharedPreferences
        userId = intent.getIntExtra("user_id", -1)

        // If not in intent, try from SharedPreferences
        if (userId == -1) {
            val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            userId = sharedPreferences.getInt("user_id", -1)
        }

        Log.d("EditProfileActivity", "User ID: $userId")

        if (userId == -1) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show()
            navigateBack()
            return
        }

        // Fetch user profile
        fetchUserProfile(userId)

        // Set click listener for the profile image
        binding.ivProfileImageUrl.setOnClickListener {
            openGallery()
        }

        // Save button click listener
        binding.btnSave.setOnClickListener {
            // Validate input fields
            if (!validateInputs()) {
                return@setOnClickListener
            }

            val user_fullname = binding.etFullName.text.toString().trim()
            val user_name = binding.etUsername.text.toString().trim()
            val user_email = binding.etEmail.text.toString().trim()
            val contact_number = binding.etContactNumber.text.toString().trim()
            val city = binding.etAddress.text.toString().trim()

            Log.d("EditProfileActivity", "Updating profile for user ID: $userId")

            // Update the user profile
            updateUserProfile(
                user_id = userId,
                user_fullname = user_fullname,
                user_name = user_name,
                user_email = user_email,
                contact_number = contact_number,
                city = city
            )
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            binding.ivProfileImageUrl.setImageURI(selectedImageUri)
        }
    }

    private fun validateInputs(): Boolean {
        val user_fullname = binding.etFullName.text.toString().trim()
        val user_name = binding.etUsername.text.toString().trim()
        val user_email = binding.etEmail.text.toString().trim()
        val contact_number = binding.etContactNumber.text.toString().trim()
        val city = binding.etAddress.text.toString().trim()

        // Check for empty fields
        if (user_fullname.isEmpty() || user_name.isEmpty() || user_email.isEmpty() ||
            contact_number.isEmpty() || city.isEmpty()) {
            Toast.makeText(
                this@EditProfilePage,
                "Please fill all required fields",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(user_email).matches()) {
            Toast.makeText(this@EditProfilePage, "Invalid email format", Toast.LENGTH_SHORT)
                .show()
            return false
        }

        // Make contact number validation more flexible
        if (!contact_number.all { it.isDigit() }) {
            Toast.makeText(
                this@EditProfilePage,
                "Contact number must contain only digits",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }

    private fun fetchUserProfile(userId: Int) {
        Log.d("EditProfileActivity", "Fetching profile for user ID: $userId")
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.getUserProfile(userId)
                }

                Log.d("EditProfileActivity", "Profile response: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val userProfile = response.body()
                    userProfile?.let { profile ->
                        binding.etFullName.setText(profile.user_fullname ?: "")
                        binding.etUsername.setText(profile.user_name ?: "")
                        binding.etEmail.setText(profile.user_email ?: "")
                        binding.etContactNumber.setText(profile.contact_number ?: "")
                        binding.etAddress.setText(profile.city ?: "")

                        // Load profile image if available
                        profile.user_profile_url?.let { url ->
                            Glide.with(this@EditProfilePage)
                                .load(url)
                                .placeholder(R.drawable.logowhitebg___copy)
                                .into(binding.ivProfileImageUrl)
                        }

                        Log.d("EditProfileActivity", "Profile loaded successfully")
                    } ?: run {
                        Log.e("EditProfileActivity", "Profile body is null")
                        Toast.makeText(
                            this@EditProfilePage,
                            "Failed to load profile data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.e("EditProfilePage", "Failed to fetch profile: ${response.errorBody()?.string()}")
                    Toast.makeText(
                        this@EditProfilePage,
                        "Failed to fetch profile: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateBack()
                }
            } catch (e: Exception) {
                Log.e("EditProfilePage", "Network error: ${e.message}", e)
                Toast.makeText(
                    this@EditProfilePage,
                    "Network error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                navigateBack()
            }
        }
    }

    private fun updateUserProfile(
        user_id: Int,
        user_fullname: String,
        user_name: String,
        user_email: String,
        contact_number: String,
        city: String
    ) {
        Log.d("EditProfileActivity", "Updating profile...")
        CoroutineScope(Dispatchers.Main).launch {
            binding.btnSave.isEnabled = false // Prevent multiple clicks
            try {
                // Upload image if selected
                val userProfileUrl = selectedImageUri?.let { uri ->
                    uploadImage(uri)
                }

                // Create an instance of UpdateProfileRequest
                val request = UpdateProfileRequest(
                    user_id = user_id,
                    user_fullname = user_fullname,
                    user_name = user_name,
                    user_email = user_email,
                    contact_number = contact_number,
                    city = city,
                    user_profile_url = userProfileUrl
                )

                Log.d("EditProfileActivity", "Sending update request: $request")

                // Call the API with the request object
                val response = withContext(Dispatchers.IO) {
                    apiService.updateUserProfile(request)
                }

                // Log the raw response
                val rawResponse = response.raw().toString()
                Log.d("EditProfileActivity", "Raw response: $rawResponse")

                if (response.isSuccessful) {
                    val updateResponse = response.body()
                    if (updateResponse != null && updateResponse.success) {
                        Log.d("EditProfileActivity", "Profile updated successfully")
                        Toast.makeText(
                            this@EditProfilePage,
                            "Profile updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Navigate back to ProfileActivity and refresh it
                        navigateBack()
                    } else {
                        Log.e("EditProfileActivity", "Update failed: ${updateResponse?.message}")
                        Toast.makeText(
                            this@EditProfilePage,
                            "Failed to update profile: ${updateResponse?.message ?: "Unknown error"}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("EditProfileActivity", "Update failed: $errorBody")
                    Toast.makeText(
                        this@EditProfilePage,
                        "Failed to update profile: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("EditProfileActivity", "Error updating profile: ${e.message}", e)
                Toast.makeText(
                    this@EditProfilePage,
                    "Error updating profile: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.btnSave.isEnabled = true // Re-enable the button
            }
        }
    }

    private suspend fun uploadImage(imageUri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = contentResolver.openInputStream(imageUri)
                val file = File(cacheDir, "temp_image.jpg")
                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val response = apiService.uploadImage(body)
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.url
                } else {
                    Log.e("EditProfileActivity", "Image upload failed: ${response.errorBody()?.string()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("EditProfileActivity", "Error uploading image: ${e.message}", e)
                null
            }
        }
    }

    private fun navigateBack() {
        val intent = Intent(this@EditProfilePage, ProfileActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}