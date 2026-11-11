package com.example.finalproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AccountActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tvAccUsername: TextView
    private lateinit var tvAccRole: TextView
    private lateinit var etAccAddress: EditText
    private lateinit var tvAccWalletBalance: TextView
    private lateinit var rgAddressPresets: RadioGroup

    private var currentUsername: String = "customer"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        dbHelper = DatabaseHelper(this)

        currentUsername = intent.getStringExtra("EXTRA_USERNAME") ?: "customer"

        val btnBack = findViewById<Button>(R.id.btnBackToDashboard)
        val btnSaveAddress = findViewById<Button>(R.id.btnSaveAddress)
        val btnOpenMaps = findViewById<Button>(R.id.btnOpenGoogleMaps)
        val btnTopUp = findViewById<Button>(R.id.btnTopUpWallet)
        val btnLogout = findViewById<Button>(R.id.btnAccLogout)

        tvAccUsername = findViewById(R.id.tvAccUsername)
        tvAccRole = findViewById(R.id.tvAccRole)
        etAccAddress = findViewById(R.id.etAccAddress)
        tvAccWalletBalance = findViewById(R.id.tvAccWalletBalance)
        rgAddressPresets = findViewById(R.id.rgAddressPresets)

        btnBack.setOnClickListener {
            finish()
        }

        rgAddressPresets.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbPresetUni -> etAccAddress.setText("123 University Belt, Sampaloc, Manila")
                R.id.rbPresetHome -> etAccAddress.setText("456 España Blvd, Manila")
                R.id.rbPresetOffice -> etAccAddress.setText("789 Makati Ave, Makati City")
            }
        }

        btnSaveAddress.setOnClickListener {
            val newAddress = etAccAddress.text.toString().trim()
            if (newAddress.isEmpty()) {
                Toast.makeText(this, "Please enter a valid address.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val success = dbHelper.updateUserAddress(currentUsername, newAddress)
            if (success) {
                Toast.makeText(this, "Delivery address saved & updated!", Toast.LENGTH_SHORT).show()
                loadProfile()
            }
        }

        btnOpenMaps.setOnClickListener {
            val address = etAccAddress.text.toString().trim()
            val mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address.ifBlank { "Manila, Philippines" }))
            val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            try {
                startActivity(mapIntent)
            } catch (e: Exception) {
                val webMapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(address)))
                startActivity(webMapIntent)
            }
        }

        btnTopUp.setOnClickListener {
            val success = dbHelper.topUpWallet(currentUsername, 500.0)
            if (success) {
                Toast.makeText(this, "₱500.00 added to your BitePay Wallet!", Toast.LENGTH_SHORT).show()
                loadProfile()
            }
        }

        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        loadProfile()
    }

    private fun loadProfile() {
        val profile = dbHelper.getUserProfile(currentUsername)
        if (profile != null) {
            tvAccUsername.text = profile.username
            tvAccRole.text = "${profile.role} Account"
            etAccAddress.setText(profile.address)
            tvAccWalletBalance.text = "₱${String.format("%,.2f", profile.walletBalance)}"
        }
    }
}