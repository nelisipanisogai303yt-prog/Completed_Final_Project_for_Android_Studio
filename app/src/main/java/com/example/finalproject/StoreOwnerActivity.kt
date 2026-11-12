package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StoreOwnerActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tvTotalOrdersCount: TextView
    private lateinit var tvTotalRevenue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_owner)

        dbHelper = DatabaseHelper(this)

        val ownerName = intent.getStringExtra("EXTRA_USERNAME") ?: "Partner"

        val tvWelcome = findViewById<TextView>(R.id.tvOwnerWelcome)
        val btnLogout = findViewById<Button>(R.id.btnLogoutOwner)

        tvWelcome.text = "Welcome, $ownerName!"

        tvTotalOrdersCount = findViewById(R.id.tvTotalOrdersCount)
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue)

        val btnGoMealTracking = findViewById<Button>(R.id.btnGoMealTracking)
        val btnGoAccountTracking = findViewById<Button>(R.id.btnGoAccountTracking)
        val btnGoOrderTracking = findViewById<Button>(R.id.btnGoOrderTracking)

        btnGoMealTracking.setOnClickListener {
            val intent = Intent(this, MealTrackingOwnerActivity::class.java)
            startActivity(intent)
        }

        btnGoAccountTracking.setOnClickListener {
            val intent = Intent(this, AccountTrackingOwnerActivity::class.java)
            startActivity(intent)
        }

        btnGoOrderTracking.setOnClickListener {
            val intent = Intent(this, OrderTrackingOwnerActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        loadDashboardMetrics()
    }

    override fun onResume() {
        super.onResume()
        loadDashboardMetrics()
    }

    private fun loadDashboardMetrics() {
        val ordersList = dbHelper.getAllOrders()
        tvTotalOrdersCount.text = ordersList.size.toString()

        var totalSales = 0.0
        for (order in ordersList) {
            totalSales += order.totalPrice
        }
        tvTotalRevenue.text = "₱${String.format("%,.2f", totalSales)}"
    }
}
