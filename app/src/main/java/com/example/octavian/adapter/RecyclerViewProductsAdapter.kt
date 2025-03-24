package com.example.octavian.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.octavian.R
import com.example.octavian.dataClass.CartItem
import com.example.octavian.dataClass.Product
import com.example.octavian.activity.ClickListenerInit
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class RecyclerViewProductsAdapter(
    private val productList: MutableList<Product>,
    private val context: Context,
    private val userId: Int
) : RecyclerView.Adapter<RecyclerViewProductsAdapter.MyViewHolder>() {

    private val listener = ClickListenerInit()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_product_lists, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = productList[position]

        // Bind product details to the ViewHolder
        holder.bind(item)

        // Set click listener for "Add to Cart" button
        holder.tvAddcart.setOnClickListener {
            // Create CartItem properly according to its constructor
            val cartItem = CartItem(
                userId = userId,
                user_id = userId,
                product_id = item.product_id,
                quantity = 1,
                items = 1,
                price = item.price,
                pricePerItem = item.price.toInt(),
                image_path = item.image_path,
                item_title = item.item_title,
                color = item.color,
                isSelected = false // Default not selected
            )

            // Send a request to the PHP API to add the product to the cart
            addToCart(holder, cartItem, userId)
        }
    }

    private fun addToCart(holder: MyViewHolder, cartItem: CartItem, userId: Int) {
        val url = "http://192.168.35.15/octavian_web/APP_DB/add_to_cart.php"

        // Create a JSON object with the cart item details
        val jsonBody = JSONObject().apply {
            put("user_id", userId)
            put("product_id", cartItem.product_id)
            put("quantity", cartItem.quantity)
            put("price", cartItem.price)
            put("image_path", cartItem.image_path)
            put("item_title", cartItem.item_title)
            put("color", cartItem.color)
        }

        // Send a POST request to the PHP API
        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                Log.d("AddToCart", "Response: $response")
                Snackbar.make(holder.itemView, "${cartItem.item_title} added to cart", Snackbar.LENGTH_SHORT).show()
            },
            { error ->
                Log.e("AddToCart", "Error: ${error.message}")
                Snackbar.make(holder.itemView, "Failed to add ${cartItem.item_title} to cart", Snackbar.LENGTH_SHORT).show()
            }
        )

        // Add the request to the Volley request queue
        Volley.newRequestQueue(context).add(request)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAddcart: TextView = itemView.findViewById(R.id.tvAddcart)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvSize: TextView = itemView.findViewById(R.id.tvSize)
        val tvColor: TextView = itemView.findViewById(R.id.tvColor)
        val tvBrand: TextView = itemView.findViewById(R.id.tvBrand)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val ivProductImage: ImageView = itemView.findViewById(R.id.ivProductImage)
        val cardView: CardView = itemView.findViewById(R.id.cardView)

        fun bind(product: Product) {
            // Set product details
            tvProductName.text = product.item_title ?: "Unknown Product"
            tvPrice.text = "${product.price}"
            tvBrand.text = product.brand ?: "Unknown Brand"
            tvColor.text = product.color ?: "N/A"
            tvSize.text = product.size ?: "N/A"

            // Load product image using Glide
            product.image_path?.let { imageUrl ->
                Log.d("Glide", "Loading image from URL: $imageUrl") // Debugging
                Glide.with(itemView.context)
                    .load(imageUrl) // Use the image URL from the database
                    .placeholder(R.drawable.product_image) // Placeholder while loading
                    .error(R.drawable.error_image) // Error image if loading fails
                    .transition(DrawableTransitionOptions.withCrossFade()) // Smooth transition
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e("Glide", "Failed to load image: ${e?.message}")
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }
                    })
                    .into(ivProductImage)
            } ?: run {
                // If image_path is null, set a placeholder image
                ivProductImage.setImageResource(R.drawable.product_image)
            }
        }
    }
}