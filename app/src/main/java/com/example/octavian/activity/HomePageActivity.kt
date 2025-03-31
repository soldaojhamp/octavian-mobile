package com.example.octavian.activity

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.octavian.R
import com.example.octavian.adapter.RecyclerViewProductsAdapter
import com.example.octavian.dataClass.Product
import com.example.octavian.Api.RetrofitClient
import com.example.octavian.dataClass.CartItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomePageActivity : AppCompatActivity() {

    private lateinit var userNameTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyProductsView: LinearLayout
    private lateinit var recyclerViewProductsAdapter: RecyclerViewProductsAdapter
    private var productList = mutableListOf<Product>()
    private var userId: Int = 1 // Default value, will be replaced

    private lateinit var allCategH: LinearLayout
    private lateinit var tshirtCategH: LinearLayout
    private lateinit var shortCategH: LinearLayout
    private lateinit var pantsCategH: LinearLayout
    private lateinit var shoesCategH: LinearLayout
    private lateinit var promotionsView: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        // Initialize views
        Log.d("HomePageActivity", "Activity created")

        // Initialize category views
        allCategH = findViewById(R.id.linearLayoutAll)
        tshirtCategH = findViewById(R.id.linearLayoutTshirt)
        shortCategH = findViewById(R.id.linearLayoutShort)
        pantsCategH = findViewById(R.id.linearLayoutPants)
        shoesCategH = findViewById(R.id.linearLayoutShoes)

        // Initialize empty products view
        emptyProductsView = findViewById(R.id.layout_empty_products)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userNameTextView = findViewById(R.id.tvUsername) // Ensure this ID matches your layout

        // Get user data
        userId = sharedPreferences.getInt("user_id", -1)
        val userName = sharedPreferences.getString("user_name", "Guest") ?: "Guest"

        Log.d("HomePage", "User  ID: $userId, Name: $userName")

        // Check if user is logged in
        if (userId == -1) {
            Toast.makeText(this, "User  not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        userNameTextView.text = userName // Set the username in the TextView

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.rvProductLists)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Initialize the adapter with productList, context, userId, and cartList
        recyclerViewProductsAdapter = RecyclerViewProductsAdapter(productList, this, userId)
        recyclerView.adapter = recyclerViewProductsAdapter

        fetchProducts()
        // Set up category click listeners
        setupCategoryClickListeners()

        // Set up bottom navigation click listeners
        setupBottomNavigation()
    }

    private fun setupCategoryClickListeners() {
        allCategH.setOnClickListener {
            fetchProducts() // Fetch all products
            highlightCategory(allCategH)
        }

        tshirtCategH.setOnClickListener {
            fetchProducts("T-shirt") // Fetch T-shirt products
            highlightCategory(tshirtCategH)
        }

        shortCategH.setOnClickListener {
            fetchProducts("Shorts") // Fetch Short products
            highlightCategory(shortCategH)
        }

        pantsCategH.setOnClickListener {
            fetchProducts("Pants") // Fetch Pants products
            highlightCategory(pantsCategH)
        }

        shoesCategH.setOnClickListener {
            fetchProducts("Shoes") // Fetch Shoes products
            highlightCategory(shoesCategH)
        }
    }

    private fun fetchProducts(category: String? = null) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = if (category != null) {
                    RetrofitClient.instance.getProductsByCategory(category)
                } else {
                    RetrofitClient.instance.getProducts()
                }

                if (response.isSuccessful) {
                    response.body()?.let { products ->
                        productList.clear()
                        productList.addAll(products)
                        recyclerViewProductsAdapter.notifyDataSetChanged()

                        // Show empty view if no products, otherwise show RecyclerView
                        if (products.isEmpty()) {
                            recyclerView.visibility = View.GONE
                            emptyProductsView.visibility = View.VISIBLE
                        } else {
                            recyclerView.visibility = View.VISIBLE
                            emptyProductsView.visibility = View.GONE
                        }
                    } ?: run {
                        Toast.makeText(this@HomePageActivity, "No products found", Toast.LENGTH_SHORT).show()
                        recyclerView.visibility = View.GONE
                        emptyProductsView.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(this@HomePageActivity, "No products found", Toast.LENGTH_SHORT).show()
                    recyclerView.visibility = View.GONE
                    emptyProductsView.visibility = View.VISIBLE
                }
            } catch (t: Throwable) {
                Toast.makeText(this@HomePageActivity, "No products found", Toast.LENGTH_SHORT).show()
                recyclerView.visibility = View.GONE
                emptyProductsView.visibility = View.VISIBLE
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

    private fun highlightCategory(selected: LinearLayout) {
        val redBg: Drawable? = ContextCompat.getDrawable(this, R.drawable.categred)
        val grayBg: Drawable? = ContextCompat.getDrawable(this, R.drawable.categgray)

        // Reset all backgrounds to gray
        allCategH.background = grayBg
        tshirtCategH.background = grayBg
        shortCategH.background = grayBg
        pantsCategH.background = grayBg
        shoesCategH.background = grayBg

        // Set selected background to red
        selected.background = redBg
    }
}