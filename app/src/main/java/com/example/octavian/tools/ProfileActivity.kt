package com.example.octavian.tools

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.octavian.Api.RetrofitClient
import com.example.octavian.R
import com.example.octavian.model.UserProfileResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var logoutButton: Button
    private lateinit var editProfileButton: Button
    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvContactNumber: TextView
    private lateinit var tvAddress: TextView
    private var userId: Int = -1 // Default value, will be replaced


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // Initialize views
        logoutButton = findViewById(R.id.logoutButton)
        editProfileButton = findViewById(R.id.editProfileButton)
        tvUsername = findViewById(R.id.tvUsername)
        tvEmail = findViewById(R.id.tvEmail)
        tvContactNumber = findViewById(R.id.tvContactNumber)
        tvAddress = findViewById(R.id.tvAddress)

        // Fetch user data
        val sharedPreferences: SharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getInt("user_id", -1) // Default to -1 if not found

        // Check if user ID is valid
        if (userId == -1) {
            Toast.makeText(this, "User  not logged in", Toast.LENGTH_SHORT).show()
            finish() // Close the activity if user is not logged in
            return
        }

        // Call fetchUser Profile to get user data
        fetchUserProfile(userId)

        // Set up the click listener for the logout button
        logoutButton.setOnClickListener {
            logout()
        }

        // Set up the click listener for the edit profile button
        editProfileButton.setOnClickListener {
            // You can directly use userId here since it's already fetched
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("user_id", userId) // Pass the user ID to the EditProfileActivity
            startActivity(intent)
        }
    }

    private fun fetchUserProfile(userId: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitClient.instance.getUserProfile(userId)
                Log.d("ProfileActivity", "Response: ${response.body()}") // Log the response body
                if (response.isSuccessful) {
                    val userProfile = response.body()
                    userProfile?.let { profile ->
                        // Update the UI with the fetched user profile data
                        tvUsername.text = profile.user_name
                        tvEmail.text = profile.user_email
                        tvContactNumber.text = profile.contact_number
                        tvAddress.text = profile.city
                    } ?: run {
                        Toast.makeText(this@ProfileActivity, "Profile data is null", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ProfileActivity, "Failed to fetch profile: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logout() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitClient.instance.logout()
                if (response.isSuccessful) {
                    val logoutResponse = response.body()
                    if (logoutResponse != null) {
                        Toast.makeText(this@ProfileActivity, logoutResponse.success, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@ProfileActivity, LogInActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@ProfileActivity, "Logout failed: No response body", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ProfileActivity, "Logout failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Logout failed: ${e.message}")
                Toast.makeText(this@ProfileActivity, "Logout failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}