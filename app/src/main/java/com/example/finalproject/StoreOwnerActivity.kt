package com.example.finalproject

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class StoreOwnerActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var llOrdersContainer: LinearLayout
    private lateinit var llStoreMenuContainer: LinearLayout
    private lateinit var tvTotalOrdersCount: TextView
    private lateinit var tvTotalRevenue: TextView

    private lateinit var etMealName: EditText
    private lateinit var etMealDesc: EditText
    private lateinit var etMealPrice: EditText
    private lateinit var rbCatB: RadioButton
    private lateinit var rbCatP: RadioButton
    private lateinit var rbCatD: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_owner)

        dbHelper = DatabaseHelper(this)

        val ownerName = intent.getStringExtra("EXTRA_USERNAME") ?: "Partner"

        val tvWelcome = findViewById<TextView>(R.id.tvOwnerWelcome)
        val btnLogout = findViewById<Button>(R.id.btnLogoutOwner)

        tvWelcome.text = "Welcome, $ownerName!"

        llOrdersContainer = findViewById(R.id.llOrdersContainer)
        llStoreMenuContainer = findViewById(R.id.llStoreMenuContainer)
        tvTotalOrdersCount = findViewById(R.id.tvTotalOrdersCount)
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue)

        etMealName = findViewById(R.id.etMealName)
        etMealDesc = findViewById(R.id.etMealDesc)
        etMealPrice = findViewById(R.id.etMealPrice)
        rbCatB = findViewById(R.id.rbCatB)
        rbCatP = findViewById(R.id.rbCatP)
        rbCatD = findViewById(R.id.rbCatD)

        val btnAddMeal = findViewById<Button>(R.id.btnAddMeal)

        btnAddMeal.setOnClickListener {
            val name = etMealName.text.toString().trim()
            val desc = etMealDesc.text.toString().trim()
            val priceStr = etMealPrice.text.toString().trim()

            if (name.isEmpty() || desc.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all meal details.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceStr.toDoubleOrNull()
            if (price == null || price <= 0) {
                Toast.makeText(this, "Please enter a valid price.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val category: String
            val imageType: String

            when {
                rbCatB.isChecked -> {
                    category = "Burgers"
                    imageType = "burger"
                }
                rbCatP.isChecked -> {
                    category = "Pizza"
                    imageType = "pizza"
                }
                rbCatD.isChecked -> {
                    category = "Drinks"
                    imageType = "drink"
                }
                else -> {
                    category = "Snacks"
                    imageType = "snack"
                }
            }

            val storeName = "BiteDash Express Kitchen"
            val success = dbHelper.addMenuItem(
                storeName = storeName,
                itemName = name,
                description = desc,
                price = price,
                category = category,
                imageType = imageType
            )

            if (success) {
                Toast.makeText(this, "New meal added to menu!", Toast.LENGTH_SHORT).show()
                etMealName.text.clear()
                etMealDesc.text.clear()
                etMealPrice.text.clear()
                loadStoreMenu()
            } else {
                Toast.makeText(this, "Failed to add meal. Try again.", Toast.LENGTH_SHORT).show()
            }
        }

        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        loadStoreMenu()
        loadStoreOrders()
    }

    override fun onResume() {
        super.onResume()
        loadStoreMenu()
        loadStoreOrders()
    }

    private fun loadStoreMenu() {
        llStoreMenuContainer.removeAllViews()
        val menuList = dbHelper.searchAndSortMenuItems("", "All", "Rating")

        if (menuList.isEmpty()) {
            val emptyTv = TextView(this).apply {
                text = "No meals in menu. Add a new meal above!"
                textSize = 13f
                setTextColor(ContextCompat.getColor(context, R.color.text_muted))
                setPadding(12, 12, 12, 12)
            }
            llStoreMenuContainer.addView(emptyTv)
            return
        }

        for (meal in menuList) {
            val cardLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                background = ContextCompat.getDrawable(context, R.drawable.bg_card)
                elevation = 2f
                setPadding(16, 16, 16, 16)

                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 12)
                layoutParams = params
            }

            val imgView = ImageView(this).apply {
                val drawableRes = when (meal.imageType) {
                    "chicken" -> R.drawable.ic_food_chicken
                    "coffee" -> R.drawable.ic_food_coffee
                    "snack" -> R.drawable.ic_food_snack
                    "burger" -> R.drawable.ic_food_burger
                    "pizza" -> R.drawable.ic_food_pizza
                    "drink" -> R.drawable.ic_food_drink
                    "combo" -> R.drawable.ic_food_combo
                    else -> R.drawable.ic_bitedash_logo
                }
                setImageResource(drawableRes)
                layoutParams = LinearLayout.LayoutParams(48.dpToPx(), 48.dpToPx()).apply {
                    setMargins(0, 0, 12, 0)
                }
            }

            val infoLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            }

            val tvTitle = TextView(this).apply {
                text = "${meal.itemName} (₱${String.format("%.2f", meal.price)})"
                textSize = 15f
                setTextColor(ContextCompat.getColor(context, R.color.text_dark))
                setTypeface(null, Typeface.BOLD)
            }

            val tvDesc = TextView(this).apply {
                text = meal.description
                textSize = 12f
                setTextColor(ContextCompat.getColor(context, R.color.text_muted))
            }

            val btnDelete = Button(this).apply {
                text = "DELETE"
                textSize = 10f
                setTextColor(ContextCompat.getColor(context, R.color.red_error))
                backgroundTintList = ContextCompat.getColorStateList(context, R.color.yellow_light)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    36.dpToPx()
                ).apply {
                    setMargins(0, 4, 0, 0)
                }
                setOnClickListener {
                    dbHelper.deleteMenuItem(meal.itemId)
                    Toast.makeText(context, "Meal removed from menu.", Toast.LENGTH_SHORT).show()
                    loadStoreMenu()
                }
            }

            infoLayout.addView(tvTitle)
            infoLayout.addView(tvDesc)
            infoLayout.addView(btnDelete)

            cardLayout.addView(imgView)
            cardLayout.addView(infoLayout)

            llStoreMenuContainer.addView(cardLayout)
        }
    }

    private fun loadStoreOrders() {
        llOrdersContainer.removeAllViews()

        val ordersList = dbHelper.getAllOrders()

        tvTotalOrdersCount.text = ordersList.size.toString()

        var totalSales = 0.0
        for (order in ordersList) {
            totalSales += order.totalPrice
        }
        tvTotalRevenue.text = "₱${String.format("%.2f", totalSales)}"

        if (ordersList.isEmpty()) {
            val emptyTextView = TextView(this).apply {
                text = "No customer orders received yet."
                textSize = 14f
                setTextColor(ContextCompat.getColor(context, R.color.text_muted))
                setPadding(16, 24, 16, 24)
            }
            llOrdersContainer.addView(emptyTextView)
            return
        }

        for (order in ordersList) {
            val cardLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                background = ContextCompat.getDrawable(context, R.drawable.bg_card)
                elevation = 2f
                setPadding(20, 20, 20, 20)

                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 16)
                layoutParams = params
            }

            // Top Header Line: Order ID & Status
            val headerLine = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            val tvOrderId = TextView(this).apply {
                text = "Order #${order.orderId} • ${order.customerName}"
                textSize = 16f
                setTextColor(ContextCompat.getColor(context, R.color.text_dark))
                setTypeface(null, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            }

            val tvStatus = TextView(this).apply {
                text = order.status
                textSize = 12f
                setTextColor(ContextCompat.getColor(context, R.color.text_dark))
                setTypeface(null, Typeface.BOLD)
                background = ContextCompat.getDrawable(context, R.drawable.bg_badge_yellow)
                setPadding(16, 6, 16, 6)
            }

            headerLine.addView(tvOrderId)
            headerLine.addView(tvStatus)

            // Item details
            val tvItems = TextView(this).apply {
                text = "Item: ${order.itemName}\nPayment: ${order.paymentType}"
                textSize = 14f
                setTextColor(ContextCompat.getColor(context, R.color.text_dark))
                setPadding(0, 8, 0, 4)
            }

            // Customer Note / Suggestions if provided
            if (order.specialNote.isNotBlank()) {
                val tvNote = TextView(this).apply {
                    text = "Customer Note: \"${order.specialNote}\""
                    textSize = 13f
                    setTextColor(ContextCompat.getColor(context, R.color.text_dark))
                    background = ContextCompat.getDrawable(context, R.drawable.bg_badge_yellow)
                    setPadding(12, 8, 12, 8)
                }
                cardLayout.addView(tvNote)
            }

            // Price Line in Pesos ₱
            val tvPrice = TextView(this).apply {
                text = "Total Price: ₱${String.format("%.2f", order.totalPrice)}"
                textSize = 15f
                setTextColor(ContextCompat.getColor(context, R.color.text_dark))
                setTypeface(null, Typeface.BOLD)
                setPadding(0, 6, 0, 8)
            }

            // Interactive Order Status Controller Buttons for Store Owner
            val statusActionLine = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            val btnOutForDelivery = Button(this).apply {
                text = "Out for Delivery"
                textSize = 11f
                setTextColor(ContextCompat.getColor(context, R.color.text_dark))
                background = ContextCompat.getDrawable(context, R.drawable.bg_yellow_button)
                layoutParams = LinearLayout.LayoutParams(0, 38.dpToPx(), 1f).apply {
                    setMargins(0, 0, 6, 0)
                }
                setOnClickListener {
                    dbHelper.updateOrderStatus(order.orderId, "Out for Delivery")
                    Toast.makeText(context, "Order #${order.orderId} is Out for Delivery!", Toast.LENGTH_SHORT).show()
                    loadStoreOrders()
                }
            }

            val btnMarkDelivered = Button(this).apply {
                text = "Mark Delivered"
                textSize = 11f
                setTextColor(ContextCompat.getColor(context, R.color.text_dark))
                background = ContextCompat.getDrawable(context, R.drawable.bg_badge_yellow)
                layoutParams = LinearLayout.LayoutParams(0, 38.dpToPx(), 1f).apply {
                    setMargins(6, 0, 0, 0)
                }
                setOnClickListener {
                    dbHelper.updateOrderStatus(order.orderId, "Delivered")
                    Toast.makeText(context, "Order #${order.orderId} marked as Delivered!", Toast.LENGTH_SHORT).show()
                    loadStoreOrders()
                }
            }

            statusActionLine.addView(btnOutForDelivery)
            statusActionLine.addView(btnMarkDelivered)

            cardLayout.addView(headerLine)
            cardLayout.addView(tvItems)
            cardLayout.addView(tvPrice)
            cardLayout.addView(statusActionLine)

            llOrdersContainer.addView(cardLayout)
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}