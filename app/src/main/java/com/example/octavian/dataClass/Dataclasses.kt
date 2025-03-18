package com.example.octavian.dataClass

import android.os.Parcel
import android.os.Parcelable

data class Product(
    val user_id: Int,
    val quantity: Int,
    val product_id: Int,
    val category: String,
    val item_title: String,
    val brand: String,
    val size: String?,
    val shoe_size: String?,
    val price: Double,
    val discounted_price: Double,
    val image_path: String,
    val color: String,
) {

}


// val description: String? // Optional description of the item
//)fun Product.formattedPrice(): String {
//    return String.format("₱%.2f", price)
//}

//data class CartItem(
//    var userId: Int? = null, // Add this line
//    val productId: Int,
//    val quantity: Int,
//    val items: Int,
//    val price: Double,
//    val image_path: String,
//    val item_title: String,
//    val color: String,
//    var isSelected: Boolean = false
//)


data class CartItem(
    var userId: Int? = null,
    val user_id: Int,
    val product_id: Int,
    val quantity: Int,
    val items: Int,
    val price: Double,
    val image_path: String?,
    val item_title: String,
    val color: String,
    var isSelected: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte() // Convert byte to Boolean
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(userId)
        parcel.writeInt(user_id)
        parcel.writeInt(product_id)
        parcel.writeInt(quantity)
        parcel.writeInt(items)
        parcel.writeDouble(price)
        parcel.writeString(image_path)
        parcel.writeString(item_title)
        parcel.writeString(color)
        parcel.writeByte(if (isSelected) 1 else 0) // Convert Boolean to byte
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartItem> {
        override fun createFromParcel(parcel: Parcel): CartItem {
            return CartItem(parcel)
        }

        override fun newArray(size: Int): Array<CartItem?> {
            return arrayOfNulls(size)
        }
    }
    fun totalPrice(): Double {
        return items * price
    }
    // Data class for OrderItem
    data class OrderItem(
        val item_title: String,
        val pricePerItem: Double,
        val color: String,
        val status: String,
        val image_path: String
    )
}


