package com.example.octavian

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.octavian.adapter.RecyclerViewProductsAdapter
import com.example.octavian.dataClass.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        setupEdgeToEdge()
        setupRecyclerView()
        loadSampleProduct()
        loadProduct()
        setupButtons()
    }

    private fun setupEdgeToEdge(){

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.rvProductLists)
        recyclerViewProductsAdapter = RecyclerViewProductsAdapter(productList)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = recyclerViewProductsAdapter
    }

    private fun loadSampleProduct() {
        val sampleProduct = mutableListOf(
            Product("", "Classic T-shirt", "", "T-shirt", "No Brand", "Black", "L", 500)
        )
        productList.addAll(sampleProduct)
        recyclerViewProductsAdapter.notifyDataSetChanged()
    }

    private fun loadProduct() {
        product_id ?: run {
            Toast.makeText(this, "Loading Product, Sample Product show", Toast.LENGTH_SHORT).show()
            return
        }
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val response = RetrofitInstance.api.getShopItems(product_id!!)
//                Log.d("HomePageActivity", "API Response: $response")
//
//                if (response.success) {
//                    withContext(Dispatchers.Main) {
//                        productList.clear()
//                        response.items?.map { item ->
//                            Product(
//                                product_id = item.product_id,
//                                image_path = item.image_path,
//                                category = item.item_price,
//                                item_title = item.item_title,
//                                brand = item.brand,
//                                color = item.color,
//                                size = item.item_image,
//                                price = item.price
//                            )
//                        }?.let { productList.addAll(it) }
//                        recyclerViewProductsAdapter.notifyDataSetChanged()
//                    }
//                } else {
//                    withContext(Dispatchers.Main) {
//                        showError(response.message ?: "Unknown error")
//                    }
//                }
//            } catch (e: HttpException) {
//                Log.e("HomePageActivity", "HTTP Error: ${e.message()}")
//                withContext(Dispatchers.Main) {
//                    showError("Network error: ${e.message()}")
//                }
//            } catch (e: Exception) {
//                Log.e("HomePageActivity", "Error: ${e.message}")
//                withContext(Dispatchers.Main) {
//                    showError("Error: ${e.message}")
//                }
//            }
//        }
    }

    private fun setupButtons() {
        val homeIcon = findViewById<ImageView>(R.id.ivicon)
        homeIcon.setOnClickListener {

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
