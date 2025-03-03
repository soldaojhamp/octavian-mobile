package com.example.octavian

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductAdapter(private val productList: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.productName)
        val brand: TextView = view.findViewById(R.id.productBrand)
        val color: TextView = view.findViewById(R.id.productColor)
        val size: TextView = view.findViewById(R.id.productSize)
        val price: TextView = view.findViewById(R.id.productPrice)
        val image: ImageView = view.findViewById(R.id.productImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.name.text = product.name
        holder.brand.text = product.brand
        holder.color.text = "Color: ${product.color}"
        holder.size.text = "Size: ${product.size}"
        holder.price.text = "₱${product.price}"

        val Glide = null
        Glide.with(holder.itemView.context).load(product.imageUrl).into(holder.image)
    }

    override fun getItemCount(): Int = productList.size
}
