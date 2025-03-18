package com.example.octavian.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.octavian.R
import com.example.octavian.dataClass.CartItem

class RecyclerViewCartAdapter(
    private val cartList: MutableList<CartItem>,
    private val onItemChecked: (CartItem, Boolean) -> Unit, // Callback for item selection
    private val onAddToCartClick: (CartItem) -> Unit // Callback for add to cart action
) : RecyclerView.Adapter<RecyclerViewCartAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_cart_lists, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = cartList[position]
        holder.tvProductName.text = item.item_title
        holder.tvColor.text = item.color
        holder.tvPrice.text = item.price.toString()

        // Log the image_path for debugging
        Log.d("RecyclerViewCartAdapter", "Image path: ${item.image_path}")

        // Load image using Glide
        if (!item.image_path.isNullOrEmpty()) {
            Log.d("RecyclerViewCartAdapter", "Loading image with Glide: ${item.image_path}")
            Glide.with(holder.itemView.context)
                .load(item.image_path)
                .placeholder(R.drawable.placeholder_image) // Placeholder while loading
                .error(R.drawable.error_image) // Error image if loading fails
                .into(holder.ivCartImage)
        } else {
            Log.d("RecyclerViewCartAdapter", "Image path is null or empty, using placeholder")
            holder.ivCartImage.setImageResource(R.drawable.placeholder_image)
        }

        // Set checkbox state and listener
        holder.checkBox.isChecked = item.isSelected
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            item.isSelected = isChecked
            onItemChecked(item, isChecked)
        }
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
        val ivCartImage: ImageView = itemView.findViewById(R.id.ivCartImage)
        val tvProductName: TextView = itemView.findViewById(R.id.textView21)
        val tvColor: TextView = itemView.findViewById(R.id.textView26)
        val tvPrice: TextView = itemView.findViewById(R.id.textView30)
    }
}