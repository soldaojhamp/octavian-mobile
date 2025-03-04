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
import com.example.octavian.global.GlobalVariables.CARTLIST
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
//        val product = ProductList[position]
//        val productId = product.product_id
//        val productImage = product.image_path
//        val productCategory = product.category
//        val productTitle = product.item_title
//        val productBrand = product.brand
//        val productColor = product.color
//        val productSize = product.size
//        val productPrice = product.price

        val item = ProductList.get(position)
        holder.tvProductName.text =item.item_title
        holder.tvPrice.text = "₱${item.price}"
        holder.tvBrand.text = item.brand
        holder.tvColor.text = item.color
        holder.tvSize.text = item.size


        holder.tvAddcart.setOnClickListener{
            val cartItem = CartItem(
                product_id = item.product_id,
                image_path = item.image_path,
                item_title = item.item_title,
                items = 1,
                pricePerItem = item.price
            )

            if(!GlobalVariables.CARTLIST.contains(cartItem)){
                listener.onCartClick(cartItem)
                Snackbar.make(holder.itemView, "${item.item_title} added to cart", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(holder.itemView, "${item.item_title} is already in cart", Snackbar.LENGTH_SHORT).show()
            }
        }


    }


    override fun getItemCount(): Int {
        return ProductList.size
    }

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