package com.example.octavian

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.octavian.adapter.RecyclerViewProductsAdapter
import com.example.octavian.dataClass.Product

class HomePageActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewProductsAdapter: RecyclerViewProductsAdapter
    private var productList = mutableListOf<Product>()
    private var displayedList = mutableListOf<Product>()

    private lateinit var allCategH: LinearLayout
    private lateinit var tshirtCategH: LinearLayout
    private lateinit var shortCategH: LinearLayout
    private lateinit var pantsCategH: LinearLayout
    private lateinit var shoesCategH: LinearLayout
    private lateinit var promotionsView: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.rvProductLists)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerViewProductsAdapter = RecyclerViewProductsAdapter(displayedList)
        recyclerView.adapter = recyclerViewProductsAdapter

        allCategH = findViewById(R.id.AllCategH)
        tshirtCategH = findViewById(R.id.TshirtCategH)
        shortCategH = findViewById(R.id.ShortCategH)
        pantsCategH = findViewById(R.id.PantsCategH)
        shoesCategH = findViewById(R.id.ShoesCategH)
        promotionsView = findViewById(R.id.promotions)

        loadSampleProduct()
        filterProducts("All")
        highlightCategory(allCategH)

        allCategH.setOnClickListener {
            filterProducts("All")
            showToast("Showing All Products")
            highlightCategory(it as LinearLayout)
        }
        tshirtCategH.setOnClickListener {
            filterProducts("T-shirt")
            showToast("Showing T-shirts")
            highlightCategory(it as LinearLayout)
        }
        shortCategH.setOnClickListener {
            filterProducts("Short")
            showToast("Showing Shorts")
            highlightCategory(it as LinearLayout)
        }
        pantsCategH.setOnClickListener {
            filterProducts("Pants")
            showToast("Showing Pants")
            highlightCategory(it as LinearLayout)
        }
        shoesCategH.setOnClickListener {
            filterProducts("Shoes")
            showToast("Showing Shoes")
            highlightCategory(it as LinearLayout)
        }

        setupBottomNav()
    }

    private fun loadSampleProduct() {
        productList.add(Product("", "Classic T-shirt", "", "T-shirt", "No Brand", "Black", "L", 500))
        productList.add(Product("", "Casual Shorts", "", "Short", "No Brand", "Gray", "M", 350))
        productList.add(Product("", "Slim Fit Pants", "", "Pants", "Brand A", "Blue", "32", 800))
        productList.add(Product("", "Running Shoes", "", "Shoes", "Brand B", "White", "42", 1200))
        productList.add(Product("", "Oversized T-shirt", "", "T-shirt", "Brand C", "White", "XL", 600))
    }

    private fun filterProducts(category: String) {
        displayedList.clear()
        if (category == "All") {
            promotionsView.visibility = View.VISIBLE
            displayedList.addAll(productList)
        } else {
            promotionsView.visibility = View.GONE
            displayedList.addAll(productList.filter { it.productCategory == category })
        }
        recyclerViewProductsAdapter.notifyDataSetChanged()
    }

    private fun highlightCategory(selected: LinearLayout) {
        val redBg: Drawable? = ContextCompat.getDrawable(this, R.drawable.categred)
        val grayBg: Drawable? = ContextCompat.getDrawable(this, R.drawable.categgray)

        allCategH.background = grayBg
        tshirtCategH.background = grayBg
        shortCategH.background = grayBg
        pantsCategH.background = grayBg
        shoesCategH.background = grayBg

        selected.background = redBg
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupBottomNav() {
        val homeIcon = findViewById<ImageView>(R.id.ivicon)
        homeIcon.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }

        val ordersIcon = findViewById<ImageView>(R.id.imageView6)
        ordersIcon.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }

        val cartIcon = findViewById<ImageView>(R.id.imageView7)
        cartIcon.setOnClickListener {
            val intent = Intent(this, CartPageActivity::class.java)
            startActivity(intent)
        }

        val profileIcon = findViewById<ImageView>(R.id.imageView8)
        profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
