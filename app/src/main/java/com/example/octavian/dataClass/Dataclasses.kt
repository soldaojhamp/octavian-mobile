package com.example.octavian.dataClass

data class Product(
    val id: String,
    val productName: String,
    val productImage: String,
    val productCategory: String, // This is the correct field
    val productBrand: String,
    val productColor: String,
    val productSize: String,
    val productPrice: Int
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
    val pricePerItem: Int, // Adding price for calculation
    val color: String
) {
    fun totalPrice(): Int {
        return items * pricePerItem
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