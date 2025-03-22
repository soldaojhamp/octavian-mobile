package com.example.octavian.tools

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.octavian.Api.ApiService
import com.example.octavian.R
import com.example.octavian.model.UpdateProfileRequest
import com.example.octavian.model.UserProfileResponse

class EditProfilePage : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etContactNumber: EditText
    private lateinit var etAddress: EditText
    private lateinit var btnSave: Button
    private var userId: Int = -1

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.35.15/octavian_web/APP_DB/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_page)

        Log.d("EditProfilePage", "Activity created")

        // Initialize views
        etFullName = findViewById(R.id.etFullName)
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etContactNumber = findViewById(R.id.etContactNumber)
        etAddress = findViewById(R.id.etAddress)
        btnSave = findViewById(R.id.btnSave)

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

        btnSave.setOnClickListener {
            // Validate input fields
            if (!validateInputs()) {
                return@setOnClickListener
            }

            val user_fullname = etFullName.text.toString().trim()
            val user_name = etUsername.text.toString().trim()
            val user_email = etEmail.text.toString().trim()
            val contact_number = etContactNumber.text.toString().trim()
            val city = etAddress.text.toString().trim()

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

    private fun validateInputs(): Boolean {
        val user_fullname = etFullName.text.toString().trim()
        val user_name = etUsername.text.toString().trim()
        val user_email = etEmail.text.toString().trim()
        val contact_number = etContactNumber.text.toString().trim()
        val city = etAddress.text.toString().trim()

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
                        etFullName.setText(profile.user_fullname ?: "")
                        etUsername.setText(profile.user_name ?: "")
                        etEmail.setText(profile.user_email ?: "")
                        etContactNumber.setText(profile.contact_number ?: "")
                        etAddress.setText(profile.city ?: "")

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
            btnSave.isEnabled = false // Prevent multiple clicks
            try {
                // Create an instance of UpdateProfileRequest
                val request = UpdateProfileRequest(
                    user_id = user_id,
                    user_fullname = user_fullname,
                    user_name = user_name,
                    user_email = user_email,
                    contact_number = contact_number,
                    city = city
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

                        // Navigate back to ProfileActivity
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
                btnSave.isEnabled = true // Re-enable the button
            }
        }
    }

    private fun navigateBack() {
        val intent = Intent(this@EditProfilePage, ProfileActivity::class.java)
        startActivity(intent)
        finish()
    }
}