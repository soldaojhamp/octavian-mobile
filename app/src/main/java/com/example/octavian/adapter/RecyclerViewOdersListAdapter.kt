package com.example.octavian.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.octavian.R
import com.example.octavian.dataClass.CartItem
import com.google.android.material.snackbar.Snackbar

class RecyclerViewOrdersListAdapter(
    private val orderList: MutableList<CartItem.OrderItem>
) : RecyclerView.Adapter<RecyclerViewOrdersListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_order_lists, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = orderList[position]
        holder.tvProductName.text = item.item_title
        holder.tvPrice.text = "${item.pricePerItem}"
        holder.tvColor.text = item.color
        holder.tvStatus.text = item.status

        // Load the image if necessary (e.g., using Glide or Picasso)
        // Glide.with(holder.itemView.context).load(item.image_path).into(holder.ivProductImage)

        holder.tvCancel.setOnClickListener {
            Snackbar.make(holder.itemView, "Order ${item.item_title} canceled", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProductImage: ImageView = itemView.findViewById(R.id.imageView15)
        val tvProductName: TextView = itemView.findViewById(R.id.textView21)
        val tvColor: TextView = itemView.findViewById(R.id.textView26)
        val tvPrice: TextView = itemView.findViewById(R.id.textView30)
        val tvStatus: TextView = itemView.findViewById(R.id.textView28)
        val tvCancel: TextView = itemView.findViewById(R.id.textView29)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }
}
