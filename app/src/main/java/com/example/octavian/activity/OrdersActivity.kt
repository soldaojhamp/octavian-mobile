package com.example.octavian.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.octavian.Api.RetrofitClient
import com.example.octavian.R
import com.example.octavian.adapter.RecyclerViewOrdersListAdapter
import com.example.octavian.dataClass.CartItem
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrdersActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerViewOrdersListAdapter
    private val allOrders = mutableListOf<CartItem.OrderItem>()
    private var userId: Int = -1

    private lateinit var filterAll: TextView
    private lateinit var filterPending: TextView
    private lateinit var filterShipped: TextView
    private lateinit var filterCompleted: TextView
    private lateinit var filterCancelled: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_orders)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.orders)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        initViews()

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerViewOrders)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecyclerViewOrdersListAdapter(mutableListOf())
        recyclerView.adapter = adapter

        // Get user ID
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getInt("user_id", -1)

        // Load initial data
        fetchOrderItems()
    }

    private fun initViews() {
        findViewById<ImageView>(R.id.backButton).setOnClickListener { finish() }

        filterAll = findViewById(R.id.textViewAll)
        filterPending = findViewById(R.id.textViewPending)
        filterShipped = findViewById(R.id.textViewShipped)
        filterCompleted = findViewById(R.id.textViewCompleted)
        filterCancelled = findViewById(R.id.textViewCancelled)

        // Set click listeners
        filterAll.setOnClickListener { filterOrders("All") }
        filterPending.setOnClickListener { filterOrders("Pending") }
        filterShipped.setOnClickListener { filterOrders("Shipped") }
        filterCompleted.setOnClickListener { filterOrders("Completed") }
        filterCancelled.setOnClickListener { filterOrders("Cancelled") }    }

    private fun fetchOrderItems() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getOrderItems(userId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { items ->
                            allOrders.clear()
                            allOrders.addAll(items)
                            filterOrders("All") // Show all by default
                        } ?: run {
                            Toast.makeText(this@OrdersActivity, "No orders found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@OrdersActivity, "Failed to load orders: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@OrdersActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private var lastClickTime = 0L

    private fun filterOrders(status: String) {
        if (System.currentTimeMillis() - lastClickTime < 500) return
        lastClickTime = System.currentTimeMillis()

        updateFilterSelection(status) // This keeps the tab highlighted

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = if (status == "All") {
                    RetrofitClient.instance.getOrderItems(userId)
                } else {
                    RetrofitClient.instance.getOrderItemsByStatus(userId, status.lowercase())
                }

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { items ->
                            adapter.updateList(items)
                            // No toast shown for empty results
                        } ?: adapter.updateList(emptyList())
                    } else {
                        Log.e("API", "Error: ${response.errorBody()?.string()}")
                        adapter.updateList(emptyList()) // Show empty state on error
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("API", "Network error", e)
                    adapter.updateList(emptyList()) // Show empty state on network error
                }
            }
        }
    }

    private fun updateFilterSelection(status: String) {
        // Reset all colors to black
        listOf(filterAll, filterPending, filterShipped, filterCompleted, filterCancelled).forEach {
            it.setTextColor(ContextCompat.getColor(this, R.color.black))
        }

        // Highlight selected filter
        when (status) {
            "All" -> filterAll.setTextColor(ContextCompat.getColor(this, R.color.red))
            "Pending" -> filterPending.setTextColor(ContextCompat.getColor(this, R.color.red))
            "Shipped" -> filterShipped.setTextColor(ContextCompat.getColor(this, R.color.red))
            "Completed" -> filterCompleted.setTextColor(ContextCompat.getColor(this, R.color.red))
            "Cancelled" -> filterCancelled.setTextColor(ContextCompat.getColor(this, R.color.red))
        }
    }
}