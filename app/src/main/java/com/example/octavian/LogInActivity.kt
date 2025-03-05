package com.example.octavian

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class LogInActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        emailEditText = findViewById(R.id.loginUsernameTxt)
        passwordEditText = findViewById(R.id.loginPasswordTxt)
        loginButton = findViewById(R.id.LoginBtn)

        loginButton.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            return
        }

        val json = JSONObject()
        json.put("user_email", email) // Ensure this matches your PHP script
        json.put("password", password)

        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())

        val request = Request.Builder()
            .url("http://192.168.24.15/final_admin_api/app_login.php") // Ensure this points to your PHP script
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("LogInActivity", "Network error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@LogInActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        Log.e("LogInActivity", "Login failed: ${response.code}")
                        runOnUiThread {
                            Toast.makeText(this@LogInActivity, "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val responseData = response.body?.string()
                        if (responseData != null) {
                            try {
                                val jsonResponse = JSONObject(responseData)

                                runOnUiThread {
                                    if (jsonResponse.getBoolean("success")) {
                                        Toast.makeText(this@LogInActivity, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show()
                                        // Navigate to the next activity
                                        val intent = Intent(this@LogInActivity, HomePageActivity::class.java)
                                        startActivity(intent)
                                        finish() // Optional: finish the login activity
                                    } else {
                                        Toast.makeText(this@LogInActivity, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("LogInActivity", "JSON parsing error: ${e.message}")
                                runOnUiThread {
                                    Toast.makeText(this@LogInActivity, "Error parsing response", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Log.e("LogInActivity", "Response body is null")
                            runOnUiThread {
                                Toast.makeText(this@LogInActivity, "Response body is null", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        })
    }
}