package com.example.octavian.tools

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.octavian.Api.RetrofitClient
import com.example.octavian.R
import com.example.octavian.adapter.RecyclerViewCartAdapter
import com.example.octavian.dataClass.CartItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartPageActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewCartAdapter: RecyclerViewCartAdapter
    private var cartList = mutableListOf<CartItem>()
    private var userId: Int = -1 // Default value, will be replaced

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_page)


        // Retrieve user ID from SharedPreferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getInt("user_id", -1) // Default to -1 if not found

        // Check if user ID is valid
        if (userId == -1) {
            Toast.makeText(this, "User  not logged in", Toast.LENGTH_SHORT).show()
            finish() // Close the activity if user is not logged in
            return
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.rvCartLists) // Ensure this matches the ID in your layout
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter with the onAddToCartClick callback
        recyclerViewCartAdapter = RecyclerViewCartAdapter(cartList, { item, isChecked ->
            // Handle item selection
            if (isChecked) {
                // Add to selected items
            } else {
                // Remove from selected items
            }
        }, { cartItem ->
            // Handle add to cart action
            addToCart(cartItem)
        })

        recyclerView.adapter = recyclerViewCartAdapter // Set the adapter here

        // Fetch cart items for the current user
        fetchCartItems(userId)

        val cartbackButton = findViewById<ImageView>(R.id.cartbackButton)
        cartbackButton.setOnClickListener {
            // Create an Intent to go back to HomePageActivity
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
            finish() // Optional: finish the current activity
        }

        // Set up the checkout button
        val checkoutButton = findViewById<Button>(R.id.checkoutButton)
        checkoutButton.setOnClickListener {
            checkoutSelectedItems()
        }
    }

    private fun checkoutSelectedItems() {
        // Gather selected items
        val selectedItems = cartList.filter { it.isSelected }

        if (selectedItems.isNotEmpty()) {
            // Create an Intent to start CheckoutActivity
            val intent = Intent(this, CheckOutActivity::class.java)
            // Pass the selected items (you may need to convert them to a Parcelable or Serializable)
            intent.putParcelableArrayListExtra("SELECTED_ITEMS", ArrayList(selectedItems))
            startActivity(intent)
        } else {
            Toast.makeText(this, "No items selected for checkout", Toast.LENGTH_SHORT).show()
        }

}

    private fun fetchCartItems(userId: Int) {
        RetrofitClient.instance.getCartItems(userId).enqueue(object : Callback<List<CartItem>> {
            override fun onResponse(call: Call<List<CartItem>>, response: Response<List<CartItem>>) {
                if (response.isSuccessful) {
                    response.body()?.let { items ->
                        if (items.isEmpty()) {
                            Toast.makeText(this@CartPageActivity, "Your cart is empty", Toast.LENGTH_SHORT).show()
                        } else {
                            cartList.clear()
                            cartList.addAll(items)
                            recyclerViewCartAdapter.notifyDataSetChanged()
                        }
                    } ?: run {
                        // Handle case where response body is null
                        Toast.makeText(this@CartPageActivity, "No items found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle error response
                    Toast.makeText(this@CartPageActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CartItem>>, t: Throwable) {
                Toast.makeText(this@CartPageActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Function to add an item to the cart
    private fun addToCart(cartItem: CartItem) {
        RetrofitClient.instance.addToCart(cartItem).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CartPageActivity, "Item added to cart", Toast.LENGTH_SHORT).show()
                    // Optionally refresh the cart items
                    fetchCartItems(userId)
                } else {
                    Toast.makeText(this@CartPageActivity, "Failed to add item to cart: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@CartPageActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}