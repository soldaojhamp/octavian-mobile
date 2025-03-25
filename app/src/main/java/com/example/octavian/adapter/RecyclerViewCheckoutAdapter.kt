package com.example.octavian.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.octavian.R
import com.example.octavian.dataClass.CartItem

class RecyclerViewCheckoutAdapter(
    private val checkoutList: MutableList<CartItem.CheckoutItem>
) : RecyclerView.Adapter<RecyclerViewCheckoutAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_checkout_lists, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = checkoutList[position]
        Log.d("CartDebug", "Item at $position: $item")

        holder.tvItemTitle.text = item.item_title
        holder.tvPrice.text = "${item.price}"
        holder.tvColor.text = item.color

        // Load the image using Glide
        Glide.with(holder.itemView.context)
            .load(item.image_path)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(holder.imageView15)
    }

    override fun getItemCount(): Int {
        return checkoutList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView15: ImageView = itemView.findViewById(R.id.imageView15)
        val tvItemTitle: TextView = itemView.findViewById(R.id.tvItemTitle)
        val tvColor: TextView = itemView.findViewById(R.id.tvColor)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }
}