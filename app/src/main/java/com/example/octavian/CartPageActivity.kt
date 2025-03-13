package com.example.octavian

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.octavian.adapter.RecyclerViewCartAdapter
import com.example.octavian.dataClass.CartItem

class CartPageActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewCartAdapter: RecyclerViewCartAdapter
    private val cartList = mutableListOf<CartItem>()
    private lateinit var tvTotal: TextView
    private lateinit var btnCheckout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart_page)

        // Adjust layout for system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cart)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Handle Back Button
        findViewById<ImageView>(R.id.cartbackButton).setOnClickListener {
            finish() // Close activity and return
        }

        // Setup RecyclerView
        recyclerView = findViewById(R.id.rvCartList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerViewCartAdapter = RecyclerViewCartAdapter(cartList)
        recyclerView.adapter = recyclerViewCartAdapter

        // Initialize total text and checkout button
        tvTotal = findViewById(R.id.tvcarttotal)
        btnCheckout = findViewById(R.id.bcartcheckout)

        // Load sample cart items
        loadSampleCartItems()

        // Handle Checkout Button
        btnCheckout.setOnClickListener {
            if (cartList.isNotEmpty()) {
                // Process checkout (You can add logic to proceed with checkout)
                tvTotal.text = "Processing Checkout..."
            } else {
                tvTotal.text = "Cart is empty!"
            }
        }
    }

    private fun loadSampleCartItems() {
        cartList.add(
            CartItem(
                product_id = "1",
                image_path = "R.drawable.tyson_tshirt",
                item_title = "Classic T-Shirt",
                items = 1,
                pricePerItem = 560,
                color = "Black"
            )
        )
        cartList.add(
            CartItem(
                product_id = "2",
                image_path = "R.drawable.wallet",
                item_title = "Leather Wallet",
                items = 2,
                pricePerItem = 300,
                color = "Brown"
            )
        )
        recyclerViewCartAdapter.notifyDataSetChanged()
        updateTotalPrice()
    }

    private fun updateTotalPrice() {
        val total = cartList.sumOf { it.pricePerItem * it.items }
        tvTotal.text = "Total: ₱$total"
    }
}
