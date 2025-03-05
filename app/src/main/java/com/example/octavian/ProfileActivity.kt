package com.example.octavian

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class ProfileActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        val logoutButton: Button = findViewById(R.id.logoutBtn)
        logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        val request = Request.Builder()
            .url("http://192.168.24.15/final_admin_api/app_logout.php") // Ensure this points to your PHP script
            .post(RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), "{}")) // Sending an empty JSON body
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ProfileActivity", "Network error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@ProfileActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        Log.e("ProfileActivity", "Logout failed: ${response.code}")
                        runOnUiThread {
                            Toast.makeText(this@ProfileActivity, "Logout failed", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val responseData = response.body?.string()
                        if (responseData != null) {
                            try {
                                val jsonResponse = JSONObject(responseData)

                                runOnUiThread {
                                    if (jsonResponse.getBoolean("success")) {
                                        Toast.makeText(this@ProfileActivity, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show()
                                        // Navigate back to the LoginActivity
                                        val intent = Intent(this@ProfileActivity, LogInActivity::class.java)
                                        startActivity(intent)
                                        finish() // Optional: finish the profile activity
                                    } else {
                                        Toast.makeText(this@ProfileActivity, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("ProfileActivity", "JSON parsing error: ${e.message}")
                                runOnUiThread {
                                    Toast.makeText(this@ProfileActivity, "Error parsing response", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Log.e("ProfileActivity", "Response body is null")
                            runOnUiThread {
                                Toast.makeText(this@ProfileActivity, "Response body is null", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        })
    }
}