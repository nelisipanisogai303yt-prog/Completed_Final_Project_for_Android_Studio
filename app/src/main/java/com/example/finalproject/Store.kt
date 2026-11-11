package com.example.finalproject

data class Store(
    val storeId: Int,
    val storeName: String,
    val category: String,
    val rating: Double,
    val deliveryTimeMins: Int,
    val deliveryFee: Double,
    val distanceKm: Double,
    val imageType: String
)