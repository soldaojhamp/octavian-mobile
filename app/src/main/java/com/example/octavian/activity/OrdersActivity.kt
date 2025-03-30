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
import com.example.octavian.dataClass.CancelOrderRequest
import com.example.octavian.dataClass.CartItem
import com.example.octavian.dataClass.CompleteOrderRequest
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

        initViews()
        setupRecyclerView()
        loadUserId()
        fetchOrderItems()
    }



    private fun initViews() {
        findViewById<ImageView>(R.id.backButton).setOnClickListener { finish() }

        filterAll = findViewById(R.id.textViewAll)
        filterPending = findViewById(R.id.textViewPending)
        filterShipped = findViewById(R.id.textViewShipped)
        filterCompleted = findViewById(R.id.textViewCompleted)
        filterCancelled = findViewById(R.id.textViewCancelled)

        filterAll.setOnClickListener { filterOrders("All") }
        filterPending.setOnClickListener { filterOrders("Pending") }
        filterShipped.setOnClickListener { filterOrders("Shipped") }
        filterCompleted.setOnClickListener { filterOrders("Completed") }
        filterCancelled.setOnClickListener { filterOrders("Cancelled") }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewOrders)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecyclerViewOrdersListAdapter(mutableListOf()).apply {
            setOrderActionListener(object : RecyclerViewOrdersListAdapter.OrderActionListener {
                override fun onCancelOrder(orderId: Int) {
                    Log.d("OrdersActivity", "Cancel order $orderId")
                    cancelOrder(orderId)
                }

                override fun onCompleteOrder(orderId: Int) {
                    Log.d("OrdersActivity", "Complete order $orderId")
                    completeOrder(orderId)
                }
            })
        }
        recyclerView.adapter = adapter
    }

    private fun loadUserId() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getInt("user_id", -1).also {
            if (it == -1) Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchOrderItems() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getOrderItems(userId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { items ->
                            allOrders.clear()
                            allOrders.addAll(items)
                            filterOrders("All")
                        } ?: showToast("No orders found")
                    } else {
                        showToast("No orders found")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Error: ${e.localizedMessage}")
                }
            }
        }
    }

    private var lastClickTime = 0L

    private fun filterOrders(status: String) {
        if (System.currentTimeMillis() - lastClickTime < 500) return
        lastClickTime = System.currentTimeMillis()

        updateFilterSelection(status)

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
                            if (items.isEmpty()) showSnackbar("No $status orders found")
                        } ?: run {
                            adapter.updateList(emptyList())
                            showSnackbar("No orders found")
                        }
                    } else {
                        Log.e("API", "Error: ${response.errorBody()?.string()}")
                        adapter.updateList(emptyList())
                        showSnackbar("No orders found")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("API", "Network error", e)
                    adapter.updateList(emptyList())
                    showSnackbar("Network error")
                }
            }
        }
    }

    private fun updateFilterSelection(status: String) {
        listOf(filterAll, filterPending, filterShipped, filterCompleted, filterCancelled).forEach {
            it.setTextColor(ContextCompat.getColor(this, R.color.black))
        }

        when (status) {
            "All" -> filterAll.setTextColor(ContextCompat.getColor(this, R.color.red))
            "Pending" -> filterPending.setTextColor(ContextCompat.getColor(this, R.color.red))
            "Shipped" -> filterShipped.setTextColor(ContextCompat.getColor(this, R.color.red))
            "Completed" -> filterCompleted.setTextColor(ContextCompat.getColor(this, R.color.red))
            "Cancelled" -> filterCancelled.setTextColor(ContextCompat.getColor(this, R.color.red))
        }
    }

    internal fun cancelOrder(orderId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.cancelOrder(
                    CancelOrderRequest(userId, orderId)
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        // Find and update the order locally
                        allOrders.find { it.order_id == orderId }?.status = "cancelled"
                        // Refresh the current view
                        filterOrders(getCurrentFilter())
                        showSnackbar("Order cancelled successfully")
                    } else {
                        val error = response.errorBody()?.string() ?: "Unknown error"
                        Log.e("API", "Cancel failed: $error")
                        showSnackbar("Failed to cancel order: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("API", "Cancel error", e)
                    showSnackbar("Error: ${e.localizedMessage}")
                }
            }
        }
    }

    internal fun completeOrder(orderId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.completeOrder(
                    CompleteOrderRequest(userId, orderId)
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        // Find and update the order locally
                        allOrders.find { it.order_id == orderId }?.status = "completed"
                        // Refresh the current view
                        filterOrders(getCurrentFilter())
                        showSnackbar("Order marked as completed")
                    } else {
                        val error = response.errorBody()?.string() ?: "Unknown error"
                        Log.e("API", "Complete failed: $error")
                        showSnackbar("Failed to complete order: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("API", "Complete error", e)
                    showSnackbar("Error: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun getCurrentFilter(): String {
        return when {
            filterAll.currentTextColor == ContextCompat.getColor(this, R.color.red) -> "All"
            filterPending.currentTextColor == ContextCompat.getColor(this, R.color.red) -> "Pending"
            filterShipped.currentTextColor == ContextCompat.getColor(this, R.color.red) -> "Shipped"
            filterCompleted.currentTextColor == ContextCompat.getColor(this, R.color.red) -> "Completed"
            filterCancelled.currentTextColor == ContextCompat.getColor(this, R.color.red) -> "Cancelled"
            else -> "All"
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT).show()
    }
}