package com.example.octavian.tools

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.octavian.Api.RetrofitClient
import com.example.octavian.R
import com.example.octavian.model.LogoutResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // Initialize the logout button
        logoutButton = findViewById(R.id.logoutButton) // Ensure this matches the ID in your layout

        // Set up the click listener for the logout button
        logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        // Make a request to the logout PHP script
        RetrofitClient.instance.logout().enqueue(object : Callback<LogoutResponse> {
            override fun onResponse(call: Call<LogoutResponse>, response: Response<LogoutResponse>) {
                if (response.isSuccessful) {
                    val logoutResponse = response.body()
                    if (logoutResponse != null && logoutResponse.success) {
                        // Logout successful
                        Toast.makeText(this@ProfileActivity, logoutResponse.message, Toast.LENGTH_SHORT).show()
                        // Redirect to login activity
                        val intent = Intent(this@ProfileActivity, LogInActivity::class.java)
                        startActivity(intent)
                        finish() // Close the ProfileActivity
                    } else {
                        // Logout failed
                        Toast.makeText(this@ProfileActivity, logoutResponse?.message ?: "Logout failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle error response
                    Toast.makeText(this@ProfileActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                // Handle network failure
                Log.e("ProfileActivity", "Logout failed: ${t.message}")
                Toast.makeText(this@ProfileActivity, "Logout failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}