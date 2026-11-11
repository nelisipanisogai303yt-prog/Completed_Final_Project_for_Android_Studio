package com.example.finalproject

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CheckoutActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    private lateinit var tvAddress: TextView
    private lateinit var tvMealName: TextView
    private lateinit var tvStoreName: TextView
    private lateinit var tvBasePrice: TextView
    private lateinit var ivFoodImg: ImageView

    private lateinit var btnQtyMinus: Button
    private lateinit var btnQtyPlus: Button
    private lateinit var tvQtyCount: TextView

    private lateinit var cbExtraSauce: CheckBox
    private lateinit var cbIcedTea: CheckBox
    private lateinit var cbCutlery: CheckBox
    private lateinit var cbExpressDelivery: CheckBox

    private lateinit var rgVouchers: RadioGroup
    private lateinit var rbVoucher50: RadioButton
    private lateinit var rbVoucherFreeDel: RadioButton

    private lateinit var etCheckoutNote: EditText
    private lateinit var rbCod: RadioButton

    private lateinit var tvSubtotalPrice: TextView
    private lateinit var tvDeliveryFeePrice: TextView
    private lateinit var tvDiscountPrice: TextView
    private lateinit var tvGrandTotal: TextView

    private var currentUsername: String = "Customer"
    private var mealName: String = "Cheeseburger Deluxe Meal"
    private var storeName: String = "BiteDash Express Kitchen"
    private var basePrice: Double = 149.0
    private var imageType: String = "burger"
    private var mealQuantity: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        dbHelper = DatabaseHelper(this)

        currentUsername = intent.getStringExtra("EXTRA_USERNAME") ?: "Customer"
        mealName = intent.getStringExtra("EXTRA_MEAL_NAME") ?: "Cheeseburger Deluxe Meal"
        storeName = intent.getStringExtra("EXTRA_STORE_NAME") ?: "BiteDash Express Kitchen"
        basePrice = intent.getDoubleExtra("EXTRA_BASE_PRICE", 149.0)
        imageType = intent.getStringExtra("EXTRA_IMAGE_TYPE") ?: "burger"

        val btnBack = findViewById<Button>(R.id.btnBackFromCheckout)
        val btnChangeAddress = findViewById<Button>(R.id.btnChangeAddress)
        val btnConfirmOrder = findViewById<Button>(R.id.btnConfirmOrder)

        tvAddress = findViewById(R.id.tvCheckoutAddress)
        tvMealName = findViewById(R.id.tvCheckoutMealName)
        tvStoreName = findViewById(R.id.tvCheckoutStoreName)
        tvBasePrice = findViewById(R.id.tvCheckoutBasePrice)
        ivFoodImg = findViewById(R.id.ivCheckoutFoodImg)

        btnQtyMinus = findViewById(R.id.btnQtyMinus)
        btnQtyPlus = findViewById(R.id.btnQtyPlus)
        tvQtyCount = findViewById(R.id.tvQtyCount)

        cbExtraSauce = findViewById(R.id.cbExtraSauce)
        cbIcedTea = findViewById(R.id.cbIcedTea)
        cbCutlery = findViewById(R.id.cbCutlery)
        cbExpressDelivery = findViewById(R.id.cbExpressDelivery)

        rgVouchers = findViewById(R.id.rgVouchers)
        rbVoucher50 = findViewById(R.id.rbVoucher50)
        rbVoucherFreeDel = findViewById(R.id.rbVoucherFreeDel)

        etCheckoutNote = findViewById(R.id.etCheckoutNote)
        rbCod = findViewById(R.id.rbCod)

        tvSubtotalPrice = findViewById(R.id.tvSubtotalPrice)
        tvDeliveryFeePrice = findViewById(R.id.tvDeliveryFeePrice)
        tvDiscountPrice = findViewById(R.id.tvDiscountPrice)
        tvGrandTotal = findViewById(R.id.tvCheckoutGrandTotal)

        // Start floating background food animations
        startBackgroundFloatingAnimations()

        // Set meal details
        tvMealName.text = mealName
        tvStoreName.text = storeName
        tvBasePrice.text = "₱${String.format("%,.2f", basePrice)}"

        val drawableRes = when (imageType) {
            "chicken" -> R.drawable.ic_food_chicken
            "coffee" -> R.drawable.ic_food_coffee
            "snack" -> R.drawable.ic_food_snack
            "burger" -> R.drawable.ic_food_burger
            "pizza" -> R.drawable.ic_food_pizza
            "drink" -> R.drawable.ic_food_drink
            "combo" -> R.drawable.ic_food_combo
            else -> R.drawable.ic_bitedash_logo
        }
        ivFoodImg.setImageResource(drawableRes)

        btnBack.setOnClickListener { finish() }

        btnChangeAddress.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            intent.putExtra("EXTRA_USERNAME", currentUsername)
            startActivity(intent)
        }

        btnQtyMinus.setOnClickListener {
            if (mealQuantity > 1) {
                mealQuantity--
                tvQtyCount.text = mealQuantity.toString()
                calculateAndDisplayPrices()
            }
        }

        btnQtyPlus.setOnClickListener {
            if (mealQuantity < 20) {
                mealQuantity++
                tvQtyCount.text = mealQuantity.toString()
                calculateAndDisplayPrices()
            }
        }

        val updateListener = { _: Any, _: Boolean -> calculateAndDisplayPrices() }
        cbExtraSauce.setOnCheckedChangeListener(updateListener)
        cbIcedTea.setOnCheckedChangeListener(updateListener)
        cbCutlery.setOnCheckedChangeListener(updateListener)
        cbExpressDelivery.setOnCheckedChangeListener(updateListener)

        rgVouchers.setOnCheckedChangeListener { _, _ -> calculateAndDisplayPrices() }

        btnConfirmOrder.setOnClickListener {
            val grandTotal = calculateGrandTotal()
            val paymentMethod = if (rbCod.isChecked) "Cash on Delivery (COD)" else "E-Wallet / GCash / BitePay"
            val noteText = etCheckoutNote.text.toString().trim()

            val addOnList = mutableListOf<String>()
            if (cbExtraSauce.isChecked) addOnList.add("Extra Sauce")
            if (cbIcedTea.isChecked) addOnList.add("Iced Tea")
            if (cbCutlery.isChecked) addOnList.add("Cutlery")
            if (cbExpressDelivery.isChecked) addOnList.add("Express Delivery")

            val voucherNote = when {
                rbVoucher50.isChecked -> " [Voucher: -₱50]"
                rbVoucherFreeDel.isChecked -> " [Voucher: Free Delivery]"
                else -> ""
            }

            val qtySummary = "${mealQuantity}x $mealName"
            val itemSummary = if (addOnList.isEmpty()) {
                qtySummary + voucherNote
            } else {
                "$qtySummary (${addOnList.joinToString(", ")})$voucherNote"
            }

            val profile = dbHelper.getUserProfile(currentUsername)
            val currentAddr = profile?.address ?: "123 University Belt, Manila"

            val success = dbHelper.placeOrder(
                customerName = currentUsername,
                storeName = storeName,
                itemName = itemSummary,
                paymentType = paymentMethod,
                totalPrice = grandTotal,
                specialNote = noteText
            )

            if (success) {
                Toast.makeText(this, "Order Placed Successfully!", Toast.LENGTH_LONG).show()

                // Launch Receipt
                val receiptIntent = Intent(this, ReceiptActivity::class.java).apply {
                    putExtra("EXTRA_ORDER_ID", (101..999).random())
                    putExtra("EXTRA_CUSTOMER_NAME", currentUsername)
                    putExtra("EXTRA_ITEM_NAME", itemSummary)
                    putExtra("EXTRA_PAYMENT_METHOD", paymentMethod)
                    putExtra("EXTRA_TOTAL_PRICE", grandTotal)
                    putExtra("EXTRA_ADDRESS", currentAddr)
                }
                startActivity(receiptIntent)
                finish()
            } else {
                Toast.makeText(this, "Failed to place order. Try again.", Toast.LENGTH_SHORT).show()
            }
        }

        loadAddress()
        calculateAndDisplayPrices()
    }

    override fun onResume() {
        super.onResume()
        loadAddress()
    }

    private fun loadAddress() {
        val profile = dbHelper.getUserProfile(currentUsername)
        tvAddress.text = profile?.address ?: "123 University Belt, Sampaloc, Manila"
    }

    private fun calculateGrandTotal(): Double {
        var subtotal = basePrice * mealQuantity
        if (cbExtraSauce.isChecked) subtotal += (25.0 * mealQuantity)
        if (cbIcedTea.isChecked) subtotal += (40.0 * mealQuantity)
        if (cbExpressDelivery.isChecked) subtotal += 50.0

        var deliveryFee = 29.0
        var discount = 0.0

        when {
            rbVoucher50.isChecked -> discount = 50.0
            rbVoucherFreeDel.isChecked -> {
                deliveryFee = 0.0
                discount = 29.0
            }
        }

        val grandTotal = subtotal + deliveryFee - discount
        return if (grandTotal < 0) 0.0 else grandTotal
    }

    private fun calculateAndDisplayPrices() {
        var subtotal = basePrice * mealQuantity
        if (cbExtraSauce.isChecked) subtotal += (25.0 * mealQuantity)
        if (cbIcedTea.isChecked) subtotal += (40.0 * mealQuantity)
        if (cbExpressDelivery.isChecked) subtotal += 50.0

        var deliveryFee = 29.0
        var discount = 0.0

        when {
            rbVoucher50.isChecked -> discount = 50.0
            rbVoucherFreeDel.isChecked -> {
                deliveryFee = 0.0
                discount = 29.0
            }
        }

        val grandTotal = subtotal + deliveryFee - discount
        val finalGrandTotal = if (grandTotal < 0) 0.0 else grandTotal

        tvSubtotalPrice.text = "₱${String.format("%,.2f", subtotal)}"
        tvDeliveryFeePrice.text = "₱${String.format("%,.2f", deliveryFee)}"
        tvDiscountPrice.text = "-₱${String.format("%,.2f", discount)}"
        tvGrandTotal.text = "₱${String.format("%,.2f", finalGrandTotal)}"
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
}