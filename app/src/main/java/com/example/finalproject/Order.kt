package com.example.finalproject

data class Order(
    val orderId: Int,
    val customerName: String,
    val itemName: String,
    val paymentType: String,
    val totalPrice: Double,
    val specialNote: String = "",
    val status: String = "Order Placed",
    val rating: Int = 0,
    val review: String = ""
)