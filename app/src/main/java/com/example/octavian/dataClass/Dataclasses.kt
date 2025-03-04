package com.example.octavian.dataClass

data class Product(
    val product_id: String,     // Unique identifier for the shop item
    val image_path: String?,     // URL or resource ID for the item's image
    val category: String,
    val item_title: String,
    val brand: String,
    val color: String,
    val size: String,
    val price: Int,
)

    // val description: String? // Optional description of the item
//)fun Product.formattedPrice(): String {
//    return String.format("₱%.2f", price)
//}

data class CartItem(
    val product_id: String,
    val image_path: String?,
    val item_title: String,
    var items: Int,
    val pricePerItem: Int // Adding price for calculation
) {
    fun totalPrice(): Int {
        return items * pricePerItem
    }
}