package com.example.octavian

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.octavian.adapter.RecyclerViewEditCartAdapter
import com.example.octavian.dataClass.CartItem

class EditCartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewEditCartAdapter: RecyclerViewEditCartAdapter
    private val cartList = mutableListOf<CartItem>()
    private lateinit var checkBoxSelectAll: CheckBox
    private lateinit var btnRemoveSelected: Button
    private lateinit var btnDone: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_cart)

        // Adjust layout for system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Editcart)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Handle Back Button
        findViewById<ImageView>(R.id.EditcartbackButton).setOnClickListener {
            finish() // Close activity and return
        }

        // Handle "Done" Button
        btnDone = findViewById(R.id.tvcartEdit)
        btnDone.setOnClickListener {
            finish() // Finish edit and return to cart page
        }

        // Setup RecyclerView
        recyclerView = findViewById(R.id.rvEditCartList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerViewEditCartAdapter = RecyclerViewEditCartAdapter(cartList)
        recyclerView.adapter = recyclerViewEditCartAdapter

        // Initialize Select All and Remove Buttons
        checkBoxSelectAll = findViewById(R.id.checkboxEditcart)
        btnRemoveSelected = findViewById(R.id.button5)

        // Load sample cart items
        loadSampleCartItems()

        // Handle Select All Checkbox
        checkBoxSelectAll.setOnCheckedChangeListener { _, isChecked ->
            recyclerViewEditCartAdapter.selectAll(isChecked)
        }

        // Handle Remove Selected Button
        btnRemoveSelected.setOnClickListener {
            recyclerViewEditCartAdapter.removeSelectedItems()
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
        recyclerViewEditCartAdapter.notifyDataSetChanged()
    }
}
