package com.example.octavian.tools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.octavian.R
import com.example.octavian.dataClass.CartItem

class CheckOutActivity : AppCompatActivity() {

    private lateinit var selectedItemsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_out)

        selectedItemsContainer = findViewById(R.id.selectedItemsContainer) // Ensure this matches your layout

        // Retrieve the selected items from the intent
        val selectedItems: ArrayList<CartItem>? = intent.getParcelableArrayListExtra("SELECTED_ITEMS")

        // Display the selected items
        selectedItems?.let {
            displaySelectedItems(it)
        }
    }

    private fun displaySelectedItems(selectedItems: List<CartItem>) {
        // Clear the container before adding items
        selectedItemsContainer.removeAllViews()

        // Inflate the item layout for each selected item
        val inflater = LayoutInflater.from(this)

        for (item in selectedItems) {
            // Inflate the layout for each item
            val itemView: View = inflater.inflate(R.layout.selected_item_layout, selectedItemsContainer, false)

            // Find views in the inflated layout
            val imageView: ImageView = itemView.findViewById(R.id.ivOrderImage) // Ensure this matches your layout
            val titleTextView: TextView = itemView.findViewById(R.id.tvItemTitle) // Ensure this matches your layout
            val colorTextView: TextView = itemView.findViewById(R.id.tvColor) // Ensure this matches your layout
            val priceTextView: TextView = itemView.findViewById(R.id.tvPrice) // Ensure this matches your layout

            // Set the data for each item
            titleTextView.text = item.item_title
            colorTextView.text = "Color: ${item.color}" // Assuming you have a color property in CartItem
            priceTextView.text = "₱${item.price}" // Assuming price is a Double

            // Set the image (you may need to load it with Glide if it's a URL)
            // Glide.with(this).load(item.image_path).into(imageView)

            // Add the item view to the container
            selectedItemsContainer.addView(itemView)
        }
    }
}