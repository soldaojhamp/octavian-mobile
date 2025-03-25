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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.octavian.R
import com.example.octavian.dataClass.CartItem
import com.google.android.material.snackbar.Snackbar

class RecyclerViewOrdersListAdapter(
    private val orderList: MutableList<CartItem.OrderItem> = mutableListOf()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_EMPTY = 1
    }

    // Glide configuration
    private val glideOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .placeholder(R.drawable.placeholder_image)
        .error(R.drawable.error_image)
        .centerCrop()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_order_lists, parent, false)
                OrderViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_empty_orders, parent, false)
                EmptyViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is OrderViewHolder) {
            val item = orderList[position]
            holder.bind(item)
        }
        // EmptyViewHolder doesn't need binding
    }

    override fun getItemCount(): Int {
        return if (orderList.isEmpty()) 1 else orderList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (orderList.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_ITEM
    }

    fun updateList(newList: List<CartItem.OrderItem>) {
        orderList.clear()
        orderList.addAll(newList)
        notifyDataSetChanged()
    }

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView15)
        private val tvProductName: TextView = itemView.findViewById(R.id.textView21)
        private val tvColor: TextView = itemView.findViewById(R.id.textView26)
        private val tvPrice: TextView = itemView.findViewById(R.id.textView30)
        private val tvStatus: TextView = itemView.findViewById(R.id.textView28)
        private val tvCancel: TextView = itemView.findViewById(R.id.textView29)
        private val cardView: CardView = itemView.findViewById(R.id.cardView)

        fun bind(item: CartItem.OrderItem) {
            tvProductName.text = item.item_title
            tvPrice.text = item.pricePerItem.toString()
            tvColor.text = item.color
            tvStatus.text = item.status

            // Clean and load image
            val cleanImagePath = item.image_path?.replace(
                "img_products/img_products/",
                "img_products/"
            )

            Glide.with(itemView.context)
                .load(cleanImagePath)
                .apply(glideOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)

            tvCancel.setOnClickListener {
                Snackbar.make(itemView, "Order ${item.item_title} canceled", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}