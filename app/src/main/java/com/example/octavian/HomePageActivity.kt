package com.example.octavian

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

class HomePageActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val productList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)

        // Adjust layout for system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter(productList)
        recyclerView.adapter = productAdapter

        // Load all products initially
        loadProducts("All")

        // Bottom navigation setup
        setupBottomNavigation()

        // Category selection
        setupCategoryButtons()
    }

    private fun loadProducts(category: String) {
        val url = "http://10.0.2.2/octavian/get_products.php?category=$category"
        val Volley = null
        val requestQueue = Volley.newRequestQueue(this)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                productList.clear()
                if (response.length() > 0) {
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val product = Product(
                            id = jsonObject.getInt("id"),
                            name = jsonObject.getString("name"),
                            brand = jsonObject.getString("brand"),
                            color = jsonObject.getString("color"),
                            size = jsonObject.getString("size"),
                            price = jsonObject.getDouble("price"),
                            imageUrl = jsonObject.getString("imageUrl")
                        )
                        productList.add(product)
                    }
                } else {
                    Toast.makeText(this, "No products found in $category", Toast.LENGTH_SHORT).show()
                }
                productAdapter.notifyDataSetChanged()
            },
            { error ->
                Toast.makeText(this, "Error fetching products: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        requestQueue.add(jsonArrayRequest)
    }

    private fun JsonArrayRequest(
        value: Any,
        string: String,
        nothing: Nothing?,
        function: Any,
        function: Any
    ) {
    }

    private fun setupCategoryButtons() {
        findViewById<ImageView>(R.id.imageIcon1)?.setOnClickListener { loadProducts("All") }
        findViewById<ImageView>(R.id.imageIcon2)?.setOnClickListener { loadProducts("T-Shirt") }
        findViewById<ImageView>(R.id.imageIcon3)?.setOnClickListener { loadProducts("Shorts") }
        findViewById<ImageView>(R.id.imageIcon4)?.setOnClickListener { loadProducts("Pants") }
        findViewById<ImageView>(R.id.imageIcon5)?.setOnClickListener { loadProducts("Shoes") }
    }

    private fun setupBottomNavigation() {
        findViewById<ImageView>(R.id.ivicon)?.setOnClickListener {
            Toast.makeText(this, "Already on Home Page", Toast.LENGTH_SHORT).show()
        }
        findViewById<ImageView>(R.id.imageView6)?.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
        }
        findViewById<ImageView>(R.id.imageView7)?.setOnClickListener {
            startActivity(Intent(this, CartPageActivity::class.java))
        }
        findViewById<ImageView>(R.id.imageView8)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
