package com.example.octavian.activity

import android.content.SharedPreferences
import android.util.Log
import com.example.octavian.Api.RetrofitClient
import com.example.octavian.models.LoginResponse
import com.example.octavian.models.LoginUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthManager(private val sharedPreferences: SharedPreferences) {

    fun login(
        email: String,
        password: String,
        onSuccess: (LoginResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        // Create a LoginUser object
        val loginUser = LoginUser(email, password)

        // Use coroutines to call the suspend function
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitClient.instance.userLogin(loginUser)
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.success) {
                        // Save user_id to SharedPreferences
                        val userId = loginResponse.user_id
                        if (userId != null) {
                            val editor = sharedPreferences.edit()
                            editor.putInt("user_id", userId)
                            editor.apply()
                            Log.d("AuthManager", "user_id saved: $userId")        }

                        // Call the success callback
                        onSuccess(loginResponse)
                    } else {
                        // Handle unsuccessful login
                        onError(loginResponse?.message ?: "Login failed")
                    }
                } else {
                    // Handle API error
                    onError("Error: ${response.message()}")
                }
            } catch (t: Throwable) {
                // Handle network or other errors
                onError("Error: ${t.localizedMessage}")
            }
        }
    }
}

