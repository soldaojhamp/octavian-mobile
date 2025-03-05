package com.example.octavian

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class LogoutActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile) // Create a layout for the logout activity

        val logoutButton: Button = findViewById(R.id.logoutBtn)
        logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        val request = Request.Builder()
            .url("http://192.168.24.15/final_admin_api/app_logout.php") // Ensure this points to your PHP script
            .post(
                RequestBody.create(
                    "application/json; charset=utf-8".toMediaTypeOrNull(),
                    "{}"
                )
            ) // Sending an empty JSON body
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("LogoutActivity", "Network error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@LogoutActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        Log.e("LogoutActivity", "Logout failed: ${response.code}")
                        runOnUiThread {
                            Toast.makeText(this@LogoutActivity, "Logout failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        val responseData = response.body?.string()
                        if (responseData != null) {
                            try {
                                val jsonResponse = JSONObject(responseData)

                                runOnUiThread {
                                    if (jsonResponse.getBoolean("success")) {
                                        Toast.makeText(
                                            this@LogoutActivity,
                                            jsonResponse.getString("message"),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        // Navigate back to the LogInActivity
                                        val intent =
                                            Intent(this@LogoutActivity, LogInActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK) // Clear the activity stack
                                        startActivity(intent)
                                        finish() // Optional: finish the logout activity
                                    } else {
                                        Toast.makeText(
                                            this@LogoutActivity,
                                            jsonResponse.getString("message"),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("LogoutActivity", "JSON parsing error: ${e.message}")
                                runOnUiThread {
                                    Toast.makeText(
                                        this@LogoutActivity,
                                        "Error parsing response",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            Log.e("LogoutActivity", "Response body is null")
                            runOnUiThread {
                                Toast.makeText(
                                    this@LogoutActivity,
                                    "Response body is null",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        })
    }
}