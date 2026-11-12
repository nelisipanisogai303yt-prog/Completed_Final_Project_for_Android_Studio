package com.example.finalproject

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReceiptActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt)

        val dbHelper = DatabaseHelper(this)
        val orderId = intent.getIntExtra("EXTRA_ORDER_ID", 101)
        val liveOrder = dbHelper.getOrderById(orderId)

        val customerName = liveOrder?.customerName ?: (intent.getStringExtra("EXTRA_CUSTOMER_NAME") ?: "Customer")
        val itemName = liveOrder?.itemName ?: (intent.getStringExtra("EXTRA_ITEM_NAME") ?: "BiteDash Deluxe Meal")
        val paymentMethod = liveOrder?.paymentType ?: (intent.getStringExtra("EXTRA_PAYMENT_METHOD") ?: "COD")
        val totalPrice = liveOrder?.totalPrice ?: intent.getDoubleExtra("EXTRA_TOTAL_PRICE", 0.0)
        val address = intent.getStringExtra("EXTRA_ADDRESS") ?: "123 University Belt, Manila"
        val orderStatus = liveOrder?.status ?: (intent.getStringExtra("EXTRA_ORDER_STATUS") ?: "Preparing")

        val tvReceiptNumber = findViewById<TextView>(R.id.tvReceiptNumber)
        val tvReceiptDateTime = findViewById<TextView>(R.id.tvReceiptDateTime)
        val tvReceiptStatus = findViewById<TextView>(R.id.tvReceiptStatus)
        val tvReceiptCustomerName = findViewById<TextView>(R.id.tvReceiptCustomerName)
        val tvReceiptAddress = findViewById<TextView>(R.id.tvReceiptAddress)
        val tvReceiptItemName = findViewById<TextView>(R.id.tvReceiptItemName)
        val tvReceiptPaymentMethod = findViewById<TextView>(R.id.tvReceiptPaymentMethod)
        val tvReceiptTotalPrice = findViewById<TextView>(R.id.tvReceiptTotalPrice)
        val btnCloseReceipt = findViewById<Button>(R.id.btnCloseReceipt)

        val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
        val currentDateStr = sdf.format(Date())

        tvReceiptNumber.text = "Receipt #BD-${10000 + orderId}"
        tvReceiptDateTime.text = "Date: $currentDateStr"
        tvReceiptStatus.text = "STATUS: ${orderStatus.uppercase(Locale.getDefault())}"
        tvReceiptCustomerName.text = "Customer: $customerName"
        tvReceiptAddress.text = "Delivery Address: $address"
        tvReceiptItemName.text = "Ordered Items: $itemName"
        tvReceiptPaymentMethod.text = "Payment Method: $paymentMethod"
        tvReceiptTotalPrice.text = "₱${String.format("%,.2f", totalPrice)}"

        btnCloseReceipt.setOnClickListener {
            finish()
        }
    }
}
