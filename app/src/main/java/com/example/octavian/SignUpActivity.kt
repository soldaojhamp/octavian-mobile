package com.example.octavian

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        // Adjust system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find the button and set click listener
        val signUpButton = findViewById<Button>(R.id.button2)
        signUpButton.setOnClickListener {
            goToSignIn()
        }
    }

    private fun goToSignIn() {
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent)
        finish() // Optional: Closes SignUpActivity so the user can't go back
    }
}
