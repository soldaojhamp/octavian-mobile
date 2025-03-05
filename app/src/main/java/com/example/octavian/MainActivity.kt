package com.example.octavian

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val mainScope = MainScope() // Coroutine scope for the main thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        // Use coroutines for delayed navigation
        mainScope.launch {
            delay(1500) // 1500 milliseconds (1.5 seconds) delay
            navigateToWelcomePage()
        }

        // Apply window insets to the main view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun navigateToWelcomePage() {
        val intent = Intent(this@MainActivity, WelcomePageActivity::class.java)
        startActivity(intent)
        finish() // Close MainActivity after starting WelcomePageActivity
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel() // Cancel the coroutine scope when the activity is destroyed
    }
}