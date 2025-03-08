package com.example.octavian.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.octavian.R
import com.example.octavian.dataClass.CartItem
import com.google.android.material.snackbar.Snackbar

class RecyclerViewEditCartAdapter(
    private val cartList: MutableList<CartItem>
) : RecyclerView.Adapter<RecyclerViewEditCartAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_cart_lists, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = cartList[position]
        holder.tvProductName.text = item.item_title
        holder.tvPrice.text = "₱${item.pricePerItem}"
        holder.tvColor.text = item.color

        // Load the image if necessary (e.g., using Glide or Picasso)
        // Glide.with(holder.itemView.context).load(item.image_path).into(holder.ivProductImage)

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Snackbar.make(holder.itemView, "${item.item_title} selected", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
        val ivProductImage: ImageView = itemView.findViewById(R.id.imageView15)
        val tvProductName: TextView = itemView.findViewById(R.id.textView21)
        val tvColor: TextView = itemView.findViewById(R.id.textView26)
        val tvPrice: TextView = itemView.findViewById(R.id.textView30)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }
}
