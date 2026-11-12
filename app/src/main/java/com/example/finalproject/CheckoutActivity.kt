package com.example.finalproject

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlin.math.max
import kotlin.math.min

class CheckoutActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tvAddress: TextView
    private lateinit var llCheckoutCartContainer: LinearLayout

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        dbHelper = DatabaseHelper(this)

        currentUsername = intent.getStringExtra("EXTRA_USERNAME") ?: "Customer"

        val btnBack = findViewById<Button>(R.id.btnBackFromCheckout)
        val btnChangeAddress = findViewById<Button>(R.id.btnChangeAddress)
        val btnConfirmOrder = findViewById<Button>(R.id.btnConfirmOrder)

        tvAddress = findViewById(R.id.tvCheckoutAddress)
        llCheckoutCartContainer = findViewById(R.id.llCheckoutCartContainer)

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

        startBackgroundFloatingAnimations()

        btnBack.setOnClickListener { finish() }

        btnChangeAddress.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            intent.putExtra("EXTRA_USERNAME", currentUsername)
            startActivity(intent)
        }

        val updateListener = { _: Any, _: Boolean -> calculateAndDisplayPrices() }
        cbExtraSauce.setOnCheckedChangeListener(updateListener)
        cbIcedTea.setOnCheckedChangeListener(updateListener)
        cbCutlery.setOnCheckedChangeListener(updateListener)
        cbExpressDelivery.setOnCheckedChangeListener(updateListener)

        rgVouchers.setOnCheckedChangeListener { _, _ -> calculateAndDisplayPrices() }

        btnConfirmOrder.setOnClickListener {
            if (CartManager.items.isEmpty()) {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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

            val cartSummaryList = CartManager.items.map { "${it.quantity}x ${it.menuItem.itemName}" }
            val itemsFormatted = cartSummaryList.joinToString(" + ")

            val itemSummary = if (addOnList.isEmpty()) {
                itemsFormatted + voucherNote
            } else {
                "$itemsFormatted (${addOnList.joinToString(", ")})$voucherNote"
            }

            val primaryStoreName = CartManager.items.firstOrNull()?.menuItem?.storeName ?: "BiteDash Kitchen"

            val profile = dbHelper.getUserProfile(currentUsername)
            val currentAddr = profile?.address ?: "123 University Belt, Manila"

            val newOrderId = dbHelper.placeOrder(
                customerName = currentUsername,
                storeName = primaryStoreName,
                itemName = itemSummary,
                paymentType = paymentMethod,
                totalPrice = grandTotal,
                specialNote = noteText
            )

            if (newOrderId != -1L) {
                Toast.makeText(this, "Order Placed Successfully!", Toast.LENGTH_LONG).show()

                val orderId = newOrderId.toInt()
                CartManager.clear()

                val receiptIntent = Intent(this, ReceiptActivity::class.java).apply {
                    putExtra("EXTRA_ORDER_ID", orderId)
                    putExtra("EXTRA_CUSTOMER_NAME", currentUsername)
                    putExtra("EXTRA_ITEM_NAME", itemSummary)
                    putExtra("EXTRA_PAYMENT_METHOD", paymentMethod)
                    putExtra("EXTRA_TOTAL_PRICE", grandTotal)
                    putExtra("EXTRA_ADDRESS", currentAddr)
                    putExtra("EXTRA_ORDER_STATUS", "Preparing")
                }
                startActivity(receiptIntent)
                finish()
            } else {
                Toast.makeText(this, "Failed to place order. Try again.", Toast.LENGTH_SHORT).show()
            }
        }

        loadAddress()
        renderCheckoutCartItems()
        calculateAndDisplayPrices()
    }

    override fun onResume() {
        super.onResume()
        loadAddress()
        renderCheckoutCartItems()
        calculateAndDisplayPrices()
    }

    private fun renderCheckoutCartItems() {
        llCheckoutCartContainer.removeAllViews()

        if (CartManager.items.isEmpty()) {
            val emptyTv = TextView(this).apply {
                text = "Your cart is empty. Please add meals from the dashboard!"
                textSize = 14f
                setTextColor(ContextCompat.getColor(context, R.color.text_muted))
                setPadding(0, 12, 0, 12)
            }
            llCheckoutCartContainer.addView(emptyTv)
            return
        }

        for (cartItem in CartManager.items) {
            val itemRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, 8, 0, 8)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            val imgView = ImageView(this).apply {
                val drawableRes = when (cartItem.menuItem.imageType) {
                    "fastfood", "burger", "chicken", "combo" -> R.drawable.ic_food_fast_food
                    "ricemeal", "pizza" -> R.drawable.ic_food_rice_meals
                    "beverage", "coffee", "drink" -> R.drawable.ic_food_beverage
                    "snack" -> R.drawable.ic_food_snack
                    else -> R.drawable.ic_bitedash_logo
                }
                setImageResource(drawableRes)
                layoutParams = LinearLayout.LayoutParams(48.dpToPx(), 48.dpToPx()).apply {
                    setMargins(0, 0, 12, 0)
                }
            }

            val detailsBox = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            }

            val tvTitle = TextView(this).apply {
                text = cartItem.menuItem.itemName
                textSize = 15f
                setTextColor(ContextCompat.getColor(context, R.color.text_dark))
                setTypeface(null, Typeface.BOLD)
            }

            val tvPrice = TextView(this).apply {
                text = "₱${String.format("%,.2f", cartItem.menuItem.price)} each"
                textSize = 12f
                setTextColor(ContextCompat.getColor(context, R.color.text_muted))
            }

            detailsBox.addView(tvTitle)
            detailsBox.addView(tvPrice)

            // Quantity Controls
            val qtyRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }

            val btnMinus = Button(this).apply {
                text = "−"
                textSize = 14f
                setTextColor(ContextCompat.getColor(context, R.color.text_dark))
                background = ContextCompat.getDrawable(context, R.drawable.bg_qty_button_minus)
                layoutParams = LinearLayout.LayoutParams(36.dpToPx(), 36.dpToPx())
                setOnClickListener {
                    CartManager.removeItem(cartItem.menuItem)
                    renderCheckoutCartItems()
                    calculateAndDisplayPrices()
                }
            }

            val tvQty = TextView(this).apply {
                text = "  ${cartItem.quantity}  "
                textSize = 15f
                setTextColor(ContextCompat.getColor(context, R.color.text_dark))
                setTypeface(null, Typeface.BOLD)
            }

            val btnPlus = Button(this).apply {
                text = "+"
                textSize = 14f
                setTextColor(ContextCompat.getColor(context, R.color.text_dark))
                background = ContextCompat.getDrawable(context, R.drawable.bg_yellow_button)
                layoutParams = LinearLayout.LayoutParams(36.dpToPx(), 36.dpToPx())
                setOnClickListener {
                    CartManager.addItem(cartItem.menuItem)
                    renderCheckoutCartItems()
                    calculateAndDisplayPrices()
                }
            }

            qtyRow.addView(btnMinus)
            qtyRow.addView(tvQty)
            qtyRow.addView(btnPlus)

            itemRow.addView(imgView)
            itemRow.addView(detailsBox)
            itemRow.addView(qtyRow)

            llCheckoutCartContainer.addView(itemRow)
        }
    }

    private fun loadAddress() {
        val profile = dbHelper.getUserProfile(currentUsername)
        tvAddress.text = profile?.address ?: "123 University Belt, Sampaloc, Manila"
    }

    private fun calculateGrandTotal(): Double {
        val totalQty = CartManager.getItemCount()
        val itemsSubtotal = CartManager.getTotalPrice()

        var addOnsTotal = 0.0
        if (cbExtraSauce.isChecked) addOnsTotal += (25.0 * totalQty)
        if (cbIcedTea.isChecked) addOnsTotal += (40.0 * totalQty)
        if (cbExpressDelivery.isChecked) addOnsTotal += 50.0

        val baseDeliveryFee = if (CartManager.items.isEmpty()) 0.0 else 29.0
        val preDiscountTotal = itemsSubtotal + addOnsTotal + baseDeliveryFee

        var discount = 0.0
        when {
            rbVoucher50.isChecked && CartManager.items.isNotEmpty() -> {
                discount = min(50.0, preDiscountTotal)
            }
            rbVoucherFreeDel.isChecked && CartManager.items.isNotEmpty() -> {
                discount = baseDeliveryFee
            }
        }

        return max(0.0, preDiscountTotal - discount)
    }

    private fun calculateAndDisplayPrices() {
        val totalQty = CartManager.getItemCount()
        val itemsSubtotal = CartManager.getTotalPrice()

        var addOnsTotal = 0.0
        if (cbExtraSauce.isChecked) addOnsTotal += (25.0 * totalQty)
        if (cbIcedTea.isChecked) addOnsTotal += (40.0 * totalQty)
        if (cbExpressDelivery.isChecked) addOnsTotal += 50.0

        val baseDeliveryFee = if (CartManager.items.isEmpty()) 0.0 else 29.0
        val preDiscountTotal = itemsSubtotal + addOnsTotal + baseDeliveryFee

        var discount = 0.0
        when {
            rbVoucher50.isChecked && CartManager.items.isNotEmpty() -> {
                discount = min(50.0, preDiscountTotal)
            }
            rbVoucherFreeDel.isChecked && CartManager.items.isNotEmpty() -> {
                discount = baseDeliveryFee
            }
        }

        val grandTotal = max(0.0, preDiscountTotal - discount)

        val tvAddOnsPrice = findViewById<TextView>(R.id.tvAddOnsPrice)

        tvSubtotalPrice.text = "₱${String.format("%,.2f", itemsSubtotal)}"
        tvAddOnsPrice?.text = "₱${String.format("%,.2f", addOnsTotal)}"
        tvDeliveryFeePrice.text = "₱${String.format("%,.2f", baseDeliveryFee)}"
        tvDiscountPrice.text = "-₱${String.format("%,.2f", discount)}"
        tvGrandTotal.text = "₱${String.format("%,.2f", grandTotal)}"
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
