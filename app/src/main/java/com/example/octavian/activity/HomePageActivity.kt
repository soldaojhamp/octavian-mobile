package com.example.octavian.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.octavian.R
import com.example.octavian.adapter.RecyclerViewProductsAdapter
import com.example.octavian.dataClass.Product
import com.example.octavian.Api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomePageActivity : AppCompatActivity() {

    private lateinit var userNameTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewProductsAdapter: RecyclerViewProductsAdapter
    private var productList = mutableListOf<Product>()
    private var userId: Int = 1 // Default value, will be replaced

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        // Initialize views
        userNameTextView = findViewById(R.id.textView12)
        Log.d("HomePageActivity", "Activity created")

        // Initialize SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        //get user data
        userId = sharedPreferences.getInt("user_id", -1)
        val userName = when {
            sharedPreferences.contains("user_name") -> sharedPreferences.getString("user_name", "")
            else -> "Guest"
        }

        Log.d("HomePage", "User ID: $userId, Name: $userName")

        // Check if user is logged in
        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        userNameTextView.text = userName

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.rvProductLists)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Initialize the adapter with productList, context, and userId
        recyclerViewProductsAdapter = RecyclerViewProductsAdapter(productList, this, userId)
        recyclerView.adapter = recyclerViewProductsAdapter

        // Fetch products from the API
        fetchProducts()

        // Set up bottom navigation click listeners
        setupBottomNavigation()
    }



    private fun fetchProducts() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitClient.instance.getProducts()
                if (response.isSuccessful) {
                    response.body()?.let { products ->
                        productList.clear()
                        productList.addAll(products)
                        recyclerViewProductsAdapter.notifyDataSetChanged()
                    } ?: run {
                        Toast.makeText(this@HomePageActivity, "No products found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@HomePageActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (t: Throwable) {
                Toast.makeText(this@HomePageActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupBottomNavigation() {
        findViewById<ImageView>(R.id.ivicon).setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
        }

        findViewById<ImageView>(R.id.imageView6).setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
        }

        findViewById<ImageView>(R.id.imageView7).setOnClickListener {
            startActivity(Intent(this, CartPageActivity::class.java))
        }

        findViewById<ImageView>(R.id.imageView8).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}