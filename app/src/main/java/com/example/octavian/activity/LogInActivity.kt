package com.example.octavian.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.octavian.R

class LogInActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        // Initialize views
        emailEditText = findViewById(R.id.loginUsernameTxt)
        passwordEditText = findViewById(R.id.loginPasswordTxt)
        loginButton = findViewById(R.id.LoginBtn)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        // Initialize AuthManager
        authManager = AuthManager(sharedPreferences)

        // Set click listener for the login button
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

        // Validate email format
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        // Call the AuthManager to log in
        authManager.login(email, password, { loginResponse ->
            // Handle successful login
            Toast.makeText(this, loginResponse.message, Toast.LENGTH_SHORT).show()

            // Store user data in SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putInt("user_id", loginResponse.user_id) // Assuming user_id is part of the response
            editor.putString("user_name", loginResponse.user_name) // Assuming user_name is part of the response
            editor.apply()

            // Log the user_id
            Log.d("LogInActivity", "Retrieved user_id after login: ${loginResponse.user_id}")

            // Navigate to the next activity
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
            finish() // Optional: finish the login activity
        }, { errorMessage ->
            // Handle login error
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        })
    }

    // Function to validate email format
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }
}