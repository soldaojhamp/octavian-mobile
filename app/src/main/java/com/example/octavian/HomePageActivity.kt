package com.example.octavian

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageView

class HomePageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)

        // Adjusting the layout for system insets.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Fetch and set click listeners for the bottom navigation icons.
        val homeIcon = findViewById<ImageView>(R.id.ivicon)
        homeIcon.setOnClickListener {
            // Since you're already on HomePageActivity, you might want to refresh or do nothing.
            // For demonstration, we're restarting the HomePageActivity.
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }

        val ordersIcon = findViewById<ImageView>(R.id.imageView6)
        ordersIcon.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }

        val cartIcon = findViewById<ImageView>(R.id.imageView7)
        cartIcon.setOnClickListener {
            val intent = Intent(this, CartPageActivity::class.java)
            startActivity(intent)
        }

        val profileIcon = findViewById<ImageView>(R.id.imageView8)
        profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
