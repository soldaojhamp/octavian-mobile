package com.example.octavian

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.octavian.adapter.RecyclerViewOrdersListAdapter
import com.example.octavian.dataClass.CartItem

class OrdersActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewOrdersListAdapter: RecyclerViewOrdersListAdapter
    private val ordersList = mutableListOf<CartItem.OrderItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_orders)

        // Adjusting for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.orders)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Handle back button
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // Close activity and go back to previous screen
        }

        // Setup RecyclerView
        recyclerView = findViewById(R.id.rvorderlist)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerViewOrdersListAdapter = RecyclerViewOrdersListAdapter(ordersList)
        recyclerView.adapter = recyclerViewOrdersListAdapter

        // Load sample orders
        loadSampleOrders()
    }

    private fun loadSampleOrders() {
        ordersList.add(
            CartItem.OrderItem(
                item_title = "Classic T-Shirt",
                pricePerItem = 560.0,
                color = "Black",
                status = "Pending",
                image_path = "R.drawable.tyson_tshirt"
            )
        )
        ordersList.add(
            CartItem.OrderItem(
                item_title = "Casual Hoodie",
                pricePerItem = 1200.0,
                color = "Gray",
                status = "Shipped",
                image_path = "R.drawable.tyson_tshirt"
            )
        )
        recyclerViewOrdersListAdapter.notifyDataSetChanged()
    }
}
