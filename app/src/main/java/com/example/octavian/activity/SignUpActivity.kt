package com.example.octavian.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.octavian.Api.RetrofitClient
import com.example.octavian.models.User
import com.example.octavian.models.SignUpResponse
import com.example.octavian.databinding.ActivitySignUpBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.example.octavian.R

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val apiService = RetrofitClient.instance
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize AuthManager
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        authManager = AuthManager(sharedPreferences)

        // Set the OnApplyWindowInsetsListener on the root view
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up the click listener for the Sign Up button
        binding.SignUpButton.setOnClickListener {
            signUp()
        }

        // Set up the click listener for the login text view
        binding.textViewlogin.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }

        setupPasswordToggle()
    }

    private fun signUp() {
        val username = binding.usernameTxt.text.toString()
        val email = binding.emailTxt.text.toString()
        val password = binding.passwordTxt.text.toString()
        val confirmPassword = binding.confirmPasswordTxt.text.toString()

        // Basic validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
            return
        }

        // Password validation
        val passwordError = validatePassword(password)
        if (passwordError != null) {
            Toast.makeText(this, passwordError, Toast.LENGTH_SHORT).show()
            return
        }

        // Create a User object
        val user = User(name = username, email = email, password = password)

        Log.d("SignUpActivity", "User object created: $user")

        // Use coroutines to call the suspend function
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = apiService.signup(user)
                if (response.isSuccessful) {
                    val signUpResponse = response.body()
                    if (signUpResponse != null && signUpResponse.success) {
                        Toast.makeText(this@SignUpActivity, signUpResponse.message, Toast.LENGTH_SHORT).show()
                        // Navigate to the login activity
                        val intent = Intent(this@SignUpActivity, LogInActivity::class.java)
                        startActivity(intent)
                        finish() // Optional: finish the sign-up activity
                    } else {
                        val errorMessage = signUpResponse?.message ?: "Unknown error"
                        Log.e("SignUpActivity", "Registration failed: $errorMessage")
                        Toast.makeText(this@SignUpActivity, "Registration failed: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle different error codes
                    handleErrorResponse(response)
                }
            } catch (t: Throwable) {
                Log.e("SignUpActivity", "Registration failed: ${t.message}")
                Toast.makeText(this@SignUpActivity, "Registration failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleErrorResponse(response: Response<SignUpResponse>) {
        val errorCode = response.code()
        val errorBody = response.errorBody()?.string() ?: "No error body"

        when (errorCode) {
            400 -> Toast.makeText(this, "Bad Request", Toast.LENGTH_SHORT).show()
            401 -> Toast.makeText(this, "Unauthorized", Toast.LENGTH_SHORT).show()
            404 -> Toast.makeText(this, "Not Found", Toast.LENGTH_SHORT).show()
            500 -> Toast.makeText(this, "Server Error", Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(this, "Error $errorCode", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validatePassword(password: String): String? {
        // Check for at least one uppercase letter
        if (!password.any { it.isUpperCase() }) {
            return "Password must contain a capital letter"
        }

        // Check for at least one number or special character
        if (!password.any { it.isDigit() || !it.isLetterOrDigit() }) {
            return "Password must contain a number and special character"
        }

        // Check for minimum length
        if (password.length < 8) {
            return "Password must be at least 8 characters"
        }

        return null // No error
    }

    private fun setupPasswordToggle() {
        // Password field toggle
        binding.passwordTxt.setOnFocusChangeListener { _, hasFocus ->
            binding.passwordToggle.visibility = if (hasFocus || binding.passwordTxt.text.isNotEmpty()) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
        }

        binding.passwordToggle.setOnClickListener {
            togglePasswordVisibility(binding.passwordTxt, binding.passwordToggle)
        }

        // Confirm password field toggle
        binding.confirmPasswordTxt.setOnFocusChangeListener { _, hasFocus ->
            binding.confirmPasswordToggle.visibility = if (hasFocus || binding.confirmPasswordTxt.text.isNotEmpty()) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
        }

        binding.confirmPasswordToggle.setOnClickListener {
            togglePasswordVisibility(binding.confirmPasswordTxt, binding.confirmPasswordToggle)
        }
    }

    private fun togglePasswordVisibility(editText: EditText, toggleIcon: ImageView) {
        val selection = editText.selectionEnd // Save cursor position

        if (editText.transformationMethod == PasswordTransformationMethod.getInstance()) {
            // Show password
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            toggleIcon.setImageResource(R.drawable.ic_custom_show)
        } else {
            // Hide password
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            toggleIcon.setImageResource(R.drawable.ic_custom_hide)
        }

        editText.setSelection(selection) // Restore cursor position
    }
}