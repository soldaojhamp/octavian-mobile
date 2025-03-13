package com.example.octavian

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.octavian.adapter.RecyclerViewCartAdapter
import com.example.octavian.global.GlobalVariables

class CartPageActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewCartAdapter: RecyclerViewCartAdapter
    private val app = GlobalVariables
    private lateinit var btnbuy: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart_page)
        btnbuy = findViewById(R.id.btncheckout)

        val cartbackButton = findViewById<ImageView>(R.id.cartbackButton)
        cartbackButton.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }

//        if (app.CARTLIST.isEmpty()) {
//            showEmptyCartMessage()
//        } else {
//            // Initialize RecyclerView
//            recyclerView = findViewById(R.id.recyclerView)
//            val layoutManager = GridLayoutManager(this, 1)
//            recyclerView.layoutManager = layoutManager
//            recyclerViewCartAdapter = RecyclerViewCartAdapter(app.CARTLIST)
//            recyclerView.adapter = recyclerViewCartAdapter
//
//            loadCartItems()
//        }

        btnbuy.setOnClickListener {
            buyAll()
        }

    }

    private fun buyAll() {
        if (app.CARTLIST.isEmpty()) {
            showEmptyCartMessage()
            return
        }

    }

    private fun loadCartItems() {
        TODO("Not yet implemented")
    }

    private fun showEmptyCartMessage() {
        Toast.makeText(this, "No items in the cart", Toast.LENGTH_SHORT).show()
        //findViewById<TextView>(R.id.emptycart).visibility = View.VISIBLE // Optional: Show a view for the empty cart message
    }

}