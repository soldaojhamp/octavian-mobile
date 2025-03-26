package com.example.octavian.dataClass

import android.os.Parcel
import android.os.Parcelable


// Create a data class for the user response
data class UserResponse(
    val success: Boolean,
    val user: User,
    val error: String? = null
)

data class User(
    val user_id: Int,
    val userId: Int?,
    val user_name: String,
    val contact_number: String,
    val city: String
)

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
    var isSoldOut: Boolean = false // Indicates if the product is sold out
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
    val id: Int = 0,
    val userId: Int? = null,
    val user_id: Int = 0,
    val product_id: Int = 0,
    val quantity: Int = 0,
    val items: Int = 0,
    val price: Double = 0.0,
    val pricePerItem: Int = 0,
    val image_path: String? = null,
    val item_title: String = "",
    val color: String = "",
    var isSelected: Boolean = false,
    var isAvailable: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt() == 1
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeValue(userId)
        parcel.writeInt(user_id)
        parcel.writeInt(product_id)
        parcel.writeInt(quantity)
        parcel.writeInt(items)
        parcel.writeDouble(price)
        parcel.writeInt(pricePerItem)
        parcel.writeString(image_path)
        parcel.writeString(item_title)
        parcel.writeString(color)
        parcel.writeInt(if (isSelected) 1 else 0)
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
        val id: Int = 0,
        val userId: Int? = null,
        val user_id: Int,
        val order_id: Int,        // Unique identifier for the order
        val product_id: Int,      // Unique identifier for the product
        val quantity: Int,        // Quantity of the product ordered
        val status: String,       // Status of the order (e.g., "Pending", "Completed")
        val item_title: String,    // Title of the product
        val pricePerItem: Double, // Price per item
        val color: String,        // Color of the product
        val image_path: String     // URL or path to the product image
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readDouble(),
            parcel.readString() ?: "",
            parcel.readString() ?: ""
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(user_id)
            parcel.writeInt(order_id)
            parcel.writeInt(product_id)
            parcel.writeInt(quantity)
            parcel.writeString(status)
            parcel.writeString(item_title)
            parcel.writeDouble(pricePerItem)
            parcel.writeString(color)
            parcel.writeString(image_path)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<OrderItem> {
            override fun createFromParcel(parcel: Parcel): OrderItem {
                return OrderItem(parcel)
            }

            override fun newArray(size: Int): Array<OrderItem?> {
                return arrayOfNulls(size)
            }
        }
    }

    // CheckoutItem class nested within CartItem
    data class CheckoutItem(
        val item_title: String = "",
        val price: Double = 0.0,
        val color: String = "",
        val pricePerItem: Int = 0,
        val image_path: String? = null
    )
}