package com.example.octavian

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

    private lateinit var filterAll: TextView
    private lateinit var filterPending: TextView
    private lateinit var filterShipped: TextView
    private lateinit var filterCompleted: TextView
    private lateinit var filterCancelled: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_orders)

        // Adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.orders)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Back button logic
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener { finish() }

        // RecyclerView setup
        recyclerView = findViewById(R.id.rvorderlist)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerViewOrdersListAdapter = RecyclerViewOrdersListAdapter(mutableListOf())
        recyclerView.adapter = recyclerViewOrdersListAdapter

        // Load sample orders
        loadSampleOrders()

        // Setup filters (assuming IDs from XML)
        filterAll = findViewById(R.id.textViewAll)
        filterPending = findViewById(R.id.textViewPending)
        filterShipped = findViewById(R.id.textViewShipped)
        filterCompleted = findViewById(R.id.textViewCompleted)
        filterCancelled = findViewById(R.id.textViewCancelled)

        // Click listeners for filtering and highlight
        filterAll.setOnClickListener {
            filterOrders("All")
            highlightCategory(filterAll)
        }
        filterPending.setOnClickListener {
            filterOrders("Pending")
            highlightCategory(filterPending)
        }
        filterShipped.setOnClickListener {
            filterOrders("Shipped")
            highlightCategory(filterShipped)
        }
        filterCompleted.setOnClickListener {
            filterOrders("Completed")
            highlightCategory(filterCompleted)
        }
        filterCancelled.setOnClickListener {
            filterOrders("Cancelled")
            highlightCategory(filterCancelled)
        }

        // Default selection highlight (e.g., All)
        highlightCategory(filterAll)
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
        ordersList.add(
            CartItem.OrderItem(
                item_title = "Zip Hoodie",
                pricePerItem = 1500.0,
                color = "Black",
                status = "Completed",
                image_path = "R.drawable.tyson_tshirt"
            )
        )
        ordersList.add(
            CartItem.OrderItem(
                item_title = "Basic Tee",
                pricePerItem = 500.0,
                color = "White",
                status = "Cancelled",
                image_path = "R.drawable.tyson_tshirt"
            )
        )
        recyclerViewOrdersListAdapter.updateList(ordersList)
    }

    private fun filterOrders(status: String) {
        val filteredList = if (status == "All") {
            ordersList
        } else {
            ordersList.filter { it.status.equals(status, ignoreCase = true) }
        }
        recyclerViewOrdersListAdapter.updateList(filteredList)
    }

    private fun highlightCategory(selected: TextView) {
        val redColor = ContextCompat.getColor(this, R.color.red)
        val grayColor = ContextCompat.getColor(this, R.color.gray)

        // Reset all to gray
        filterAll.setTextColor(grayColor)
        filterPending.setTextColor(grayColor)
        filterShipped.setTextColor(grayColor)
        filterCompleted.setTextColor(grayColor)
        filterCancelled.setTextColor(grayColor)

        // Highlight selected one to red
        selected.setTextColor(redColor)
    }
}
