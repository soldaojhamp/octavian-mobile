package com.example.octavian.tools

import android.content.SharedPreferences
import android.util.Log
import com.example.octavian.Api.RetrofitClient
import com.example.octavian.models.LoginResponse
import com.example.octavian.models.LoginUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthManager(private val sharedPreferences: SharedPreferences) {

    fun login(
        email: String,
        password: String,
        onSuccess: (LoginResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        // Create a LoginUser object
        val loginUser = LoginUser(email, password)

        // Make the API call
        RetrofitClient.instance.userLogin(loginUser).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.success) {
                        // Save user_id to SharedPreferences
                        val userId = loginResponse.user_id
                        if (userId != null) {
                            val editor = sharedPreferences.edit()
                            editor.putInt("user_id", userId)
                            editor.apply()
                            Log.d("AuthManager", "user_id saved: $userId")
                        }

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
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // Handle network failure
                onError("Network error: ${t.localizedMessage}")
            }
        })
    }
}