package com.example.octavian.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.octavian.R
import com.example.octavian.databinding.ActivityLogInBinding

class LogInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogInBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root) // This should be the only setContentView

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        // Initialize AuthManager
        authManager = AuthManager(sharedPreferences)

        // Set up password toggle functionality
        setupPasswordToggle()

        // Set click listener for the login button
        binding.LoginBtn.setOnClickListener {
            login()
        }

        // Set click listener for the sign up text
        binding.textViewSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupPasswordToggle() {
        // Show/hide toggle based on focus and text
        binding.loginPasswordTxt.setOnFocusChangeListener { _, hasFocus ->
            binding.passwordToggle.visibility = if (hasFocus || binding.loginPasswordTxt.text.isNotEmpty()) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
        }

        // Handle text changes
        binding.loginPasswordTxt.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                binding.passwordToggle.visibility = if (s.isNullOrEmpty() && !binding.loginPasswordTxt.hasFocus()) {
                    View.INVISIBLE
                } else {
                    View.VISIBLE
                }
            }
        })

        // Toggle password visibility when clicked
        binding.passwordToggle.setOnClickListener {
            togglePasswordVisibility()
        }
    }

    private fun togglePasswordVisibility() {
        val selection = binding.loginPasswordTxt.selectionEnd

        if (binding.loginPasswordTxt.transformationMethod == PasswordTransformationMethod.getInstance()) {
            // Show password
            binding.loginPasswordTxt.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.passwordToggle.setImageResource(R.drawable.ic_custom_hide)
        } else {
            // Hide password
            binding.loginPasswordTxt.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.passwordToggle.setImageResource(R.drawable.ic_custom_show)
        }

        binding.loginPasswordTxt.setSelection(selection)
    }

    private fun login() {
        val email = binding.loginUsernameTxt.text.toString()
        val password = binding.loginPasswordTxt.text.toString()

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
            with(sharedPreferences.edit()) {
                putInt("user_id", loginResponse.user_id)
                putString("user_name", loginResponse.user_name)
                apply()
            }

            Log.d("LogInActivity", "User ID after login: ${loginResponse.user_id}")

            // Navigate to home activity
            startActivity(Intent(this, HomePageActivity::class.java))
            finish()
        }, { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        })
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}