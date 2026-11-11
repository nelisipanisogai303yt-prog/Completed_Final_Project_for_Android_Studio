package com.example.finalproject

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class CustomerDashboardActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etSearchFood: EditText
    private lateinit var rgCategories: RadioGroup
    private lateinit var rgSortBy: RadioGroup
    private lateinit var llFoodItemsContainer: LinearLayout
    private lateinit var llActiveOrdersContainer: LinearLayout

    private var currentCustomerName: String = "Customer"
    private var activeCategoryFilter = "All"
    private var activeSortBy = "Rating"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_dashboard)

        dbHelper = DatabaseHelper(this)

        currentCustomerName = intent.getStringExtra("EXTRA_USERNAME") ?: "Customer"

        val tvWelcome = findViewById<TextView>(R.id.tvCustomerWelcome)
        val btnAccount = findViewById<Button>(R.id.btnAccountProfile)
        val btnLogout = findViewById<Button>(R.id.btnLogoutCustomer)

        tvWelcome.text = "Welcome, $currentCustomerName!"

        etSearchFood = findViewById(R.id.etSearchFood)
        rgCategories = findViewById(R.id.rgCategories)
        rgSortBy = findViewById(R.id.rgSortBy)
        llFoodItemsContainer = findViewById(R.id.llFoodItemsContainer)
        llActiveOrdersContainer = findViewById(R.id.llActiveOrdersContainer)

        startBackgroundFloatingAnimations()

        btnAccount.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            intent.putExtra("EXTRA_USERNAME", currentCustomerName)
            startActivity(intent)
        }

        // Search text listener
        etSearchFood.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterAndRenderMeals()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Category filter listener
        rgCategories.setOnCheckedChangeListener { _, checkedId ->
            activeCategoryFilter = when (checkedId) {
                R.id.rbCatChicken -> "Chicken"
                R.id.rbCatCoffee -> "Coffee"
                R.id.rbCatDrinks -> "Drinks"
                R.id.rbCatSnacks -> "Snacks"
                R.id.rbCatRice -> "Rice Meals"
                R.id.rbCatBurgers -> "Burgers"
                R.id.rbCatPizza -> "Pizza"
                else -> "All"
            }
            filterAndRenderMeals()
        }

        // Sort option listener
        rgSortBy.setOnCheckedChangeListener { _, checkedId ->
            activeSortBy = when (checkedId) {
                R.id.rbSortPriceLow -> "PriceLow"
                R.id.rbSortFastest -> "Fastest"
                else -> "Rating"
            }
            filterAndRenderMeals()
        }

        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        filterAndRenderMeals()
        loadActiveCustomerOrders()
    }

    override fun onResume() {
        super.onResume()
        loadActiveCustomerOrders()
    }

    private fun loadActiveCustomerOrders() {
        llActiveOrdersContainer.removeAllViews()
        val userOrders = dbHelper.getCustomerOrders(currentCustomerName)
        val profile = dbHelper.getUserProfile(currentCustomerName)
        val currentAddr = profile?.address ?: "123 University Belt, Manila"

        if (userOrders.isEmpty()) {
            val emptyTv = TextView(this).apply {
                text = "No active orders. Select a meal below to place an order!"
                textSize = 13f
                setTextColor(ContextCompat.getColor(context, R.color.text_muted))
                setPadding(12, 12, 12, 12)
            }
            llActiveOrdersContainer.addView(emptyTv)
            return
        }

        for (order in userOrders) {
            val cardLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
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

            // Top Header: Order ID & Status Badge
            val headerLine = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            val tvTitle = TextView(this).apply {
                text = "Order #${order.orderId} • ₱${String.format("%.2f", order.totalPrice)}"
                textSize = 15f
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
                setPadding(14, 4, 14, 4)
            }

            headerLine.addView(tvTitle)
            headerLine.addView(tvStatus)

            val tvDetails = TextView(this).apply {
                text = "Item: ${order.itemName}\nPayment: ${order.paymentType}"
                textSize = 13f
                setTextColor(ContextCompat.getColor(context, R.color.text_muted))
                setPadding(0, 6, 0, 6)
            }

            // Action Line: Digital Receipt & Rating Buttons
            val actionLine = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            val btnReceipt = Button(this).apply {
                text = "View Receipt"
                textSize = 11f
                setTextColor(ContextCompat.getColor(context, R.color.text_dark))
                background = ContextCompat.getDrawable(context, R.drawable.bg_badge_yellow)
                layoutParams = LinearLayout.LayoutParams(0, 36.dpToPx(), 1f).apply {
                    setMargins(0, 4, 4, 0)
                }
                setOnClickListener {
                    val intent = Intent(context, ReceiptActivity::class.java).apply {
                        putExtra("EXTRA_ORDER_ID", order.orderId)
                        putExtra("EXTRA_CUSTOMER_NAME", order.customerName)
                        putExtra("EXTRA_ITEM_NAME", order.itemName)
                        putExtra("EXTRA_PAYMENT_METHOD", order.paymentType)
                        putExtra("EXTRA_TOTAL_PRICE", order.totalPrice)
                        putExtra("EXTRA_ADDRESS", currentAddr)
                    }
                    startActivity(intent)
                }
            }

            actionLine.addView(btnReceipt)

            if (order.status.equals("Delivered", ignoreCase = true) && order.rating == 0) {
                val btnRate = Button(this).apply {
                    text = "Rate 5 Stars ★"
                    textSize = 11f
                    setTextColor(ContextCompat.getColor(context, R.color.text_dark))
                    background = ContextCompat.getDrawable(context, R.drawable.bg_yellow_button)
                    layoutParams = LinearLayout.LayoutParams(0, 36.dpToPx(), 1f).apply {
                        setMargins(4, 4, 0, 0)
                    }
                    setOnClickListener {
                        dbHelper.rateOrder(order.orderId, 5, "Loved this meal!")
                        Toast.makeText(context, "Thank you for rating 5 stars!", Toast.LENGTH_SHORT).show()
                        loadActiveCustomerOrders()
                    }
                }
                actionLine.addView(btnRate)
            }

            cardLayout.addView(headerLine)
            cardLayout.addView(tvDetails)
            cardLayout.addView(actionLine)

            if (order.rating > 0) {
                val tvRated = TextView(this).apply {
                    text = "Your Rating: ${order.rating} Stars ★ (\"${order.review}\")"
                    textSize = 12f
                    setTextColor(ContextCompat.getColor(context, R.color.text_dark))
                    setTypeface(null, Typeface.BOLD)
                    setPadding(0, 6, 0, 0)
                }
                cardLayout.addView(tvRated)
            }

            llActiveOrdersContainer.addView(cardLayout)
        }
    }

    private fun filterAndRenderMeals() {
        llFoodItemsContainer.removeAllViews()
        val query = etSearchFood.text.toString()
        val mealsList = dbHelper.searchAndSortMenuItems(query, activeCategoryFilter, activeSortBy)

        if (mealsList.isEmpty()) {
            val emptyTv = TextView(this).apply {
                text = "No meals found matching your criteria."
                textSize = 14f
                setTextColor(ContextCompat.getColor(context, R.color.text_muted))
                setPadding(16, 24, 16, 24)
            }
            llFoodItemsContainer.addView(emptyTv)
            return
        }

        for (meal in mealsList) {
            val cardLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                background = ContextCompat.getDrawable(context, R.drawable.bg_card)
                elevation = 2f
                setPadding(16, 16, 16, 16)

                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 16)
                layoutParams = params
            }

            // Food Picture Icon
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
                layoutParams = LinearLayout.LayoutParams(56.dpToPx(), 56.dpToPx()).apply {
                    setMargins(0, 0, 16, 0)
                }
            }

            // Details Layout
            val infoLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            }

            // Title & Rating Line
            val tvTitle = TextView(this).apply {
                text = "${meal.itemName}  ★ ${meal.rating}"
                textSize = 16f
                setTextColor(ContextCompat.getColor(context, R.color.text_dark))
                setTypeface(null, Typeface.BOLD)
            }

            val tvStore = TextView(this).apply {
                text = "${meal.storeName} • ₱29 Delivery • ~${meal.prepTimeMins} mins"
                textSize = 12f
                setTextColor(ContextCompat.getColor(context, R.color.text_muted))
            }

            val tvDesc = TextView(this).apply {
                text = meal.description
                textSize = 13f
                setTextColor(ContextCompat.getColor(context, R.color.text_muted))
                setPadding(0, 4, 0, 4)
            }

            val tvPrice = TextView(this).apply {
                text = "₱${String.format("%.2f", meal.price)}"
                textSize = 15f
                setTextColor(ContextCompat.getColor(context, R.color.text_dark))
                setTypeface(null, Typeface.BOLD)
            }

            val btnOrder = Button(this).apply {
                text = "ORDER NOW →"
                textSize = 12f
                setTextColor(ContextCompat.getColor(context, R.color.text_dark))
                background = ContextCompat.getDrawable(context, R.drawable.bg_yellow_button)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    40.dpToPx()
                ).apply {
                    setMargins(0, 8, 0, 0)
                }
                setOnClickListener {
                    val checkoutIntent = Intent(this@CustomerDashboardActivity, CheckoutActivity::class.java).apply {
                        putExtra("EXTRA_USERNAME", currentCustomerName)
                        putExtra("EXTRA_MEAL_NAME", meal.itemName)
                        putExtra("EXTRA_STORE_NAME", meal.storeName)
                        putExtra("EXTRA_BASE_PRICE", meal.price)
                        putExtra("EXTRA_IMAGE_TYPE", meal.imageType)
                    }
                    startActivity(checkoutIntent)
                }
            }

            infoLayout.addView(tvTitle)
            infoLayout.addView(tvStore)
            infoLayout.addView(tvDesc)
            infoLayout.addView(tvPrice)
            infoLayout.addView(btnOrder)

            cardLayout.addView(imgView)
            cardLayout.addView(infoLayout)

            llFoodItemsContainer.addView(cardLayout)
        }
    }

    private fun startBackgroundFloatingAnimations() {
        val ivFloat1 = findViewById<ImageView>(R.id.ivFloatFood1)
        val ivFloat2 = findViewById<ImageView>(R.id.ivFloatFood2)
        val ivFloat3 = findViewById<ImageView>(R.id.ivFloatFood3)
        val ivFloat4 = findViewById<ImageView>(R.id.ivFloatFood4)

        animateFloatingView(ivFloat1, -35f, 2800L, 0L)
        animateFloatingView(ivFloat2, 40f, 3200L, 400L)
        animateFloatingView(ivFloat3, -45f, 3000L, 200L)
        animateFloatingView(ivFloat4, 35f, 3400L, 600L)
    }

    private fun animateFloatingView(view: ImageView, deltaY: Float, duration: Long, delay: Long) {
        val translateY = ObjectAnimator.ofFloat(view, "translationY", 0f, deltaY)
        translateY.duration = duration
        translateY.repeatCount = ValueAnimator.INFINITE
        translateY.repeatMode = ValueAnimator.REVERSE
        translateY.startDelay = delay

        val rotation = ObjectAnimator.ofFloat(view, "rotation", -10f, 10f)
        rotation.duration = duration + 400L
        rotation.repeatCount = ValueAnimator.INFINITE
        rotation.repeatMode = ValueAnimator.REVERSE
        rotation.startDelay = delay

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(translateY, rotation)
        animatorSet.start()
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}