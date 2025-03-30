package com.example.octavian.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.octavian.R
import com.example.octavian.activity.OrdersActivity
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

    interface OrderActionListener {
        fun onCancelOrder(orderId: Int)
        fun onCompleteOrder(orderId: Int)
    }

    private var orderActionListener: OrderActionListener? = null

    fun setOrderActionListener(listener: OrderActionListener) {
        this.orderActionListener = listener
    }

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
    }

    override fun getItemCount(): Int = if (orderList.isEmpty()) 1 else orderList.size

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
        private val tvCancel: TextView = itemView.findViewById(R.id.cancel_button)

        fun bind(item: CartItem.OrderItem) {
            // Set basic item info
            tvProductName.text = item.item_title
            tvPrice.text = item.pricePerItem?.toString() ?: "0"
            tvColor.text = item.color
            tvStatus.text = item.status
            tvStatus.setTextColor(when (item.status?.lowercase()) {
                "pending" -> ContextCompat.getColor(itemView.context, R.color.orange)
                "shipped" -> ContextCompat.getColor(itemView.context, R.color.blue)
                "completed" -> ContextCompat.getColor(itemView.context, R.color.green)
                "cancelled" -> ContextCompat.getColor(itemView.context, R.color.red)
                else -> ContextCompat.getColor(itemView.context, R.color.gray)
            })

            // Load image
            item.image_path?.let { imagePath ->
                val cleanPath = imagePath.replace("img_products/img_products/", "img_products/")
                Glide.with(itemView.context)
                    .load(cleanPath)
                    .apply(glideOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView)
            } ?: run {
                Glide.with(itemView.context)
                    .load(R.drawable.placeholder_image)
                    .into(imageView)
            }

            // Configure cancel/receive button based on status
            tvCancel.apply {
                when (item.status?.lowercase()) {
                    "pending" -> {
                        text = "Cancel Order"
                        isEnabled = true
                        alpha = 1f
                        visibility = View.VISIBLE
                        setOnClickListener {
                            (itemView.context as? OrdersActivity)?.cancelOrder(item.order_id ?: -1)
                        }
                    }
                    "shipped" -> {
                        text = "Order Received"
                        isEnabled = true
                        alpha = 1f
                        visibility = View.VISIBLE
                        setOnClickListener {
                            (itemView.context as? OrdersActivity)?.completeOrder(item.order_id ?: -1)
                        }
                    }
                    else -> visibility = View.GONE
                }
            }
        }
    }

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}