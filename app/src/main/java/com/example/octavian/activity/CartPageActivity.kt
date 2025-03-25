package com.example.octavian.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.octavian.Api.RetrofitClient
import com.example.octavian.R
import com.example.octavian.adapter.RecyclerViewCartAdapter
import com.example.octavian.dataClass.CartItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartPageActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewCartAdapter: RecyclerViewCartAdapter
    private var cartList = mutableListOf<CartItem>()
    private var userId: Int = -1
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_page)

        progressDialog = ProgressDialog(this).apply {
            setMessage("Loading cart...")
            setCancelable(false)
        }

        // Fetch user data
        val sharedPreferences: SharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getInt("user_id", -1) // Default to -1 if not found

        Log.d("ProfileActivity", "User ID from SharedPreferences: $userId")

        // Check if user ID is valid
        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            redirectToLogin()
            return
        }

        recyclerViewCartAdapter = RecyclerViewCartAdapter(
            cartList,
            { item, isChecked -> item.isSelected = isChecked },
            { cartItem -> addToCart(cartItem) }
        )

        initViews()
        fetchCartItems()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.rvCartLists)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerViewCartAdapter = RecyclerViewCartAdapter(
            cartList,
            { item, isChecked -> item.isSelected = isChecked },
            { cartItem -> addToCart(cartItem) }
        )
        recyclerView.adapter = recyclerViewCartAdapter

        findViewById<ImageView>(R.id.cartbackButton).setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.checkoutButton).setOnClickListener {
            checkoutSelectedItems()
        }
    }

    private fun checkoutSelectedItems() {
        val selectedItems = cartList.filter { it.isSelected }
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "No items selected", Toast.LENGTH_SHORT).show()
            return
        }

        startActivity(Intent(this, CheckOutActivity::class.java).apply {
            putParcelableArrayListExtra("SELECTED_ITEMS", ArrayList(selectedItems))
        })
    }

    private fun fetchCartItems() {
        progressDialog.show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getCartItems(userId)

                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()

                    if (response.isSuccessful) {
                        response.body()?.let { items ->
                            cartList.clear()
                            cartList.addAll(items)
                            recyclerViewCartAdapter.notifyDataSetChanged()

                            if (items.isEmpty()) {
                                Toast.makeText(
                                    this@CartPageActivity,
                                    "Your cart is empty",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@CartPageActivity,
                            "Failed to load cart: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@CartPageActivity,
                        "Error: ${e.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun addToCart(cartItem: CartItem) {
        // Check if the item is sold out
        if (!cartItem.isAvailable) {
            Toast.makeText(this, "Item is sold out.", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if the item is already in the cart
        if (cartList.any { it.product_id == cartItem.product_id }) {
            Toast.makeText(this, "Item is already in your cart.", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.addToCart(cartItem)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@CartPageActivity,
                            "Item added to cart",
                            Toast.LENGTH_SHORT
                        ).show()
                        fetchCartItems() // Refresh cart items after adding
                    } else {
                        Toast.makeText(
                            this@CartPageActivity,
                            "Failed to add item: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CartPageActivity,
                        "Network error: ${e.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, LogInActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
        finish()
    }
}