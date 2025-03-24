package com.example.octavian.activity

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.octavian.Api.RetrofitClient
import com.example.octavian.R
import com.example.octavian.adapter.RecyclerViewCheckoutAdapter
import com.example.octavian.dataClass.CartItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class CheckOutActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewCheckoutAdapter: RecyclerViewCheckoutAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var checkoutList = mutableListOf<CartItem.CheckoutItem>()
    private var userId: Int = -1
    private var selectedItems: ArrayList<CartItem>? = null
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_out)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getInt("user_id", -1)
        Log.d("ProfileActivity", "User  ID from SharedPreferences: $userId")

        // Check if user ID is valid
        if (userId == -1) {
            Toast.makeText(this, "User  not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize UI
        initViews()
        setupRecyclerView()
        displayUserInfo() // Corrected method call
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView2)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.button6).setOnClickListener {
            placeOrder()
        }

        findViewById<ImageView>(R.id.imageView5)?.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        selectedItems = intent.getParcelableArrayListExtra("SELECTED_ITEMS")
            ?: arrayListOf<CartItem>().also {
                Toast.makeText(this, "No items selected", Toast.LENGTH_SHORT).show()
                finish()
            }

        checkoutList.addAll(selectedItems!!.map {
            CartItem.CheckoutItem(
                item_title = it.item_title,
                price = it.price,
                color = it.color,
                pricePerItem = it.pricePerItem,
                image_path = it.image_path
            )
        })

        recyclerViewCheckoutAdapter = RecyclerViewCheckoutAdapter(checkoutList)
        recyclerView.adapter = recyclerViewCheckoutAdapter
        updateTotalAmount()

    }


    private fun placeOrder() {
        if (selectedItems.isNullOrEmpty()) {
            Toast.makeText(this, "No items to order", Toast.LENGTH_SHORT).show()
            return
        }

        val progressDialog = ProgressDialog(this).apply {
            setMessage("Placing order...")
            setCancelable(false)
            show()
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Prepare order data
                val orderItems = JSONArray().apply {
                    selectedItems?.forEach { item ->
                        put(JSONObject().apply {
                            put("product_id", item.product_id)
                            put("quantity", item.quantity)
                            put("price", item.price)
                        })
                    }
                }

                val orderData = JSONObject().apply {
                    put("user_id", userId)
                    put("items", JSONArray().apply {
                        selectedItems?.forEach { item ->
                            put(JSONObject().apply {
                                put("product_id", item.product_id)
                                put("quantity", item.quantity)
                                put("price", item.price)
                            })
                        }
                    })
                }

                // Create RequestBody from String
                val requestBody = orderData.toString().toRequestBody("application/json".toMediaType())

                // Make API call
                val response = RetrofitClient.instance.placeOrder(requestBody)
                // ... handle response ...
                val responseBody = response.body()

                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    if (response.isSuccessful && responseBody != null) {
                        try {
                            val json = JSONObject(responseBody.string())
                            if (json.getBoolean("success")) {
                                handleOrderSuccess(json)
                            } else {
                                handleOrderFailure(json.optString("error", "Unknown error"))
                            }
                        } catch (e: JSONException) {
                            handleOrderFailure("Invalid response format")
                        }
                    } else {
                        handleOrderFailure(response.message() ?: "Unknown error")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    handleOrderFailure(e.localizedMessage ?: "Network error")
                }
            }
        }
    }

    private fun handleOrderSuccess(response: JSONObject) {
        val insertedCount = response.optInt("items_inserted", 0)
        val deletedCount = response.optInt("items_deleted", 0)

        Log.d("CheckOut", "Order success. Inserted: $insertedCount, Deleted: $deletedCount")

        Toast.makeText(
            this,
            "Order placed successfully!\nItems processed: $insertedCount",
            Toast.LENGTH_LONG
        ).show()

        // Navigate to home
        startActivity(Intent(this, HomePageActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
        finish()
    }

    private fun handleOrderFailure(error: String) {
        Log.e("CheckOut", "Order failed: $error")
        Toast.makeText(
            this,
            "Order failed: $error",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun updateTotalAmount() {
        val totalAmount = selectedItems?.sumOf { it.price * it.quantity } ?: 0.0
        findViewById<TextView>(R.id.textView42).text = "%.2f".format(totalAmount)
    }

    private fun displayUserInfo() {
        findViewById<TextView>(R.id.textView36).text = sharedPreferences.getString("user_name", "Username")
        findViewById<TextView>(R.id.textView37).text = sharedPreferences.getString("contact_number", "Not provided")
        findViewById<TextView>(R.id.textView38).text = sharedPreferences.getString("city", "Address not provided")
    }
}