package com.example.finalproject

data class MenuItem(
    val itemId: Int,
    val storeName: String,
    val itemName: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageType: String,
    val rating: Double = 4.8,
    val prepTimeMins: Int = 15,
    val isAvailable: Boolean = true
)
