package com.example.octavian.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.octavian.dataClass.Product
import com.example.octavian.R
import com.example.octavian.tools.ClickListenerInit
import com.example.octavian.adapter.RecyclerViewProductsAdapter.MyViewHolder
import com.example.octavian.dataClass.CartItem
import com.example.octavian.global.GlobalVariables
import com.google.android.material.snackbar.Snackbar


class RecyclerViewProductsAdapter(
    private val ProductList: MutableList<Product>
) : RecyclerView.Adapter<MyViewHolder>() {
    private val listener = ClickListenerInit()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_product_lists, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = ProductList.get(position)
        holder.tvProductName.text =item.item_title
        holder.tvPrice.text = "₱${item.price}"
        holder.tvBrand.text = item.brand
        holder.tvColor.text = item.color
        holder.tvSize.text = item.size

//        // Check if the item image URL is valid
//        if (!item.image_path.isNullOrEmpty()) {
//            Picasso.get()
//                .load(item.image_path)
//                .placeholder(R.drawable.no_img_placeholder)
//                .error(R.drawable.no_img_placeholder) // Handle error
//                .into(holder.ivProductImage)
//        } else {
//            // Optionally set a default image if URL is empty
//            holder.ivProductImage.setImageResource(R.drawable.no_img_placeholder)
//        }

        holder.tvAddcart.setOnClickListener{
            val cartItem = CartItem(
                product_id = item.product_id,
                image_path = item.image_path,
                item_title = item.item_title,
                items = 1,
                color = item.color,
                pricePerItem = item.price)

            if(!GlobalVariables.CARTLIST.contains(cartItem)){
                listener.onCartClick(cartItem)
                Snackbar.make(holder.itemView, "${item.item_title} added to cart", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(holder.itemView, "${item.item_title} is already in cart", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = ProductList.size

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAddcart: TextView = itemView.findViewById(R.id.tvAddcart)
        val tvPrice : TextView = itemView.findViewById(R.id.tvPrice)
        val tvSize: TextView  = itemView.findViewById(R.id.tvSize)
        val tvColor: TextView = itemView.findViewById(R.id.tvColor)
        val tvBrand: TextView = itemView.findViewById(R.id.tvBrand)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val ivProductImage: ImageView = itemView.findViewById(R.id.ivProductImage)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }
}