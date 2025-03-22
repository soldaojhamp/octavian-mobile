package com.example.octavian.tools

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.octavian.Api.ApiService
import com.example.octavian.R
import com.example.octavian.model.UserProfileResponse
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etContactNumber: EditText
    private lateinit var etAddress: EditText
    private lateinit var btnSave: Button

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.35.15/octavian_web/APP_DB/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile_page)

        etFullName = findViewById(R.id.etFullName)
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etContactNumber = findViewById(R.id.etContactNumber)
        etAddress = findViewById(R.id.etAddress)
        btnSave = findViewById(R.id.btnSave)

        val userId = intent.getIntExtra("USER_ID", -1)
        if (userId != -1) {
            fetchUserProfile(userId)
        } else {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show()
        }

        btnSave.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val contactNumber = etContactNumber.text.toString().trim()
            val address = etAddress.text.toString().trim()

            // Validate input fields
            if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || contactNumber.isEmpty() || address.isEmpty()) {
                Toast.makeText(this@EditProfileActivity, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this@EditProfileActivity, "Invalid email format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate contact number format (example: 10 digits)
            if (contactNumber.length != 10 || !contactNumber.all { it.isDigit() }) {
                Toast.makeText(this@EditProfileActivity, "Invalid contact number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Update the user profile
            updateUserProfile(userId, fullName, username, email, contactNumber, address)
        }
    }

    private fun fetchUserProfile(userId: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = apiService.getUserProfile(userId)
                if (response.isSuccessful) {
                    val userProfile = response.body()
                    userProfile?.let { profile: UserProfileResponse -> // Explicitly specify the type
                        etFullName.setText(profile.user_fullname)
                        etUsername.setText(profile.user_name)
                        etEmail.setText(profile.user_email)
                        etContactNumber.setText(profile.contact_number)
                        etAddress.setText(profile.city)
                    }
                } else {
                    Toast.makeText(this@EditProfileActivity, "Failed to fetch profile", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditProfileActivity, "An error occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUserProfile(userId: Int, fullName: String, username: String, email: String, contactNumber: String, address: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = apiService.updateUserProfile(userId, fullName, username, email, contactNumber, address)
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@EditProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()

                    // Navigate back to ProfileActivity
                    val intent = Intent(this@EditProfileActivity, ProfileActivity::class.java)
                    intent.putExtra("USER_ID", userId) // Pass the user ID back to ProfileActivity
                    startActivity(intent)
                    finish() // Close the current activity
                } else {
                    Toast.makeText(this@EditProfileActivity, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditProfileActivity, "An error occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }
}