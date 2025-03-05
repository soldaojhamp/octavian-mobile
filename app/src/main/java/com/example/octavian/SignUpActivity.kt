package com.example.octavian

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.octavian.Api.RetrofitClient
import com.example.octavian.Model.SignUpResponse
import com.example.octavian.Model.User
import com.example.octavian.databinding.ActivitySignUpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    private val apiService = RetrofitClient.apiService
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the OnApplyWindowInsetsListener on the root view
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up the click listener for the Sign Up button
        binding.SignUpButton.setOnClickListener {
            signup()
        }

        // Set up the click listener for the login text view
        binding.textViewlogin.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signup() {
        val username = binding.usernameTxt.text.toString()
        val email = binding.emailTxt.text.toString()
        val password = binding.passwordTxt.text.toString()
        val confirmPassword = binding.confirmPasswordTxt.text.toString()

        // Basic validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a User object
        val user = User(name = username, email = email, password = password)

        // Make the API call to sign up
        apiService.signUp(user).enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(
                call: Call<SignUpResponse>,
                response: Response<SignUpResponse>
            ) {
                if (response.isSuccessful) {
                    val signUpResponse = response.body()
                    if (signUpResponse != null && signUpResponse.success) {
                        Toast.makeText(this@SignUpActivity, signUpResponse.message, Toast.LENGTH_SHORT).show()
                        // Optionally, you can access the user data
                        val user = signUpResponse.user
                        // Do something with the user data if needed
                        val intent = Intent(this@SignUpActivity, LogInActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Log the error message from the response
                        val errorMessage = signUpResponse?.message ?: "Unknown error occurred"
                        Log.e("SignUpActivity", "Registration failed: $errorMessage")
                        Toast.makeText(this@SignUpActivity, "Registration failed: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle different error codes
                    val errorCode = response.code()
                    val errorBody = response.errorBody()?.string() ?: "No error body"

                    when (errorCode) {
                        400 -> {
                            Log.e("SignUpActivity", "Error 400: Bad Request - $errorBody")
                            Toast.makeText(this@SignUpActivity, "Bad Request: Please check your input.", Toast.LENGTH_SHORT).show()
                        }
                        401 -> {
                            Log.e("SignUpActivity", "Error 401: Unauthorized - $errorBody")
                            Toast.makeText(this@SignUpActivity, "Unauthorized: Please check your credentials.", Toast.LENGTH_SHORT).show()
                        }
                        404 -> {
                            Log.e("SignUpActivity", "Error 404: Not Found - $errorBody")
                            Toast.makeText(this@SignUpActivity, "Not Found: The requested resource could not be found.", Toast.LENGTH_SHORT).show()
                        }
                        500 -> {
                            Log.e("SignUpActivity", "Error 500: Internal Server Error - $errorBody")
                            Toast.makeText(this@SignUpActivity, "Server Error: Please try again later.", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Log.e("SignUpActivity", "Error $errorCode: $errorBody")
                            Toast.makeText(this@SignUpActivity, "Error $errorCode: $errorBody", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                Log.e("SignUpActivity", "Registration failed: ${t.message}")
                Toast.makeText(this@SignUpActivity, "Registration failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}