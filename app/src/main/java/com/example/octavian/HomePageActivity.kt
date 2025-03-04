package com.example.octavian

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.octavian.adapter.RecyclerViewProductsAdapter
import com.example.octavian.dataClass.Product

class HomePageActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewProductsAdapter: RecyclerViewProductsAdapter
    private var productList = mutableListOf<Product>()
    private var product_id: String? = null
    private var varName: String? = null

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

        // Set up RecyclerView
        recyclerView = findViewById(R.id.rvProductLists)
        recyclerViewProductsAdapter = RecyclerViewProductsAdapter(productList)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = recyclerViewProductsAdapter

        loadSampleProduct() // Load sample products

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

    private fun loadSampleProduct() {
        // Use mutable list so you can add items
        val sampleProduct = mutableListOf(
            Product("", "Classic T-shirt", "", "T-shirt", "No Brand", "Black", "L", 500)
        )

        // Add the sample product to the main product list
        productList.addAll(sampleProduct)

        // Notify adapter that the data has changed so RecyclerView can update
        recyclerViewProductsAdapter.notifyDataSetChanged()
    }
}
