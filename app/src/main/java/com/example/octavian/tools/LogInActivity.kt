package com.example.octavian.tools

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.octavian.R
import com.example.octavian.databinding.ActivityLogInBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class LogInActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var binding: ActivityLogInBinding


    private val client = OkHttpClient()





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_log_in)

        emailEditText = findViewById(R.id.loginUsernameTxt)
        passwordEditText = findViewById(R.id.loginPasswordTxt)
        loginButton = findViewById(R.id.LoginBtn)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the OnApplyWindowInsetsListener on the root view
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }



        loginButton.setOnClickListener {
            login()
        }
        binding.textViewSignUp.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)

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
            .url("http://192.168.212.15/final_admin_api/APP_DB/app_login.php") // Ensure this points to your PHP script
            .post(requestBody)
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("com.example.octavian.tools.LogInActivity", "Network error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@LogInActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        Log.e("com.example.octavian.tools.LogInActivity", "Login failed: ${response.code}")
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
                                Log.e("com.example.octavian.tools.LogInActivity", "JSON parsing error: ${e.message}")
                                runOnUiThread {
                                    Toast.makeText(this@LogInActivity, "Error parsing response", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Log.e("com.example.octavian.tools.LogInActivity", "Response body is null")
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