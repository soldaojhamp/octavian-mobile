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

class RecyclerViewCheckoutAdapter(
    private val checkoutList: MutableList<CartItem.CheckoutItem>
) : RecyclerView.Adapter<RecyclerViewCheckoutAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_checkout_lists, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = checkoutList[position]
        holder.tvProductName.text = item.item_title
        holder.tvPrice.text = "₱${item.pricePerItem}"
        holder.tvColor.text = item.color

    }

    override fun getItemCount(): Int {
        return checkoutList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProductImage: ImageView = itemView.findViewById(R.id.imageView15)
        val tvProductName: TextView = itemView.findViewById(R.id.textView21)
        val tvColor: TextView = itemView.findViewById(R.id.textView26)
        val tvPrice: TextView = itemView.findViewById(R.id.textView30)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }
}
