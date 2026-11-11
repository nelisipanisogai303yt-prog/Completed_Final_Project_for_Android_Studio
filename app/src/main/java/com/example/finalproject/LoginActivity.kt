package com.example.finalproject

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegisterLink = findViewById<TextView>(R.id.tvRegisterLink)

        startBackgroundFloatingAnimations()

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both username and password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val role = dbHelper.validateLogin(username, password)

            if (role != null) {
                Toast.makeText(this, "Login successful! Welcome $username.", Toast.LENGTH_SHORT).show()

                if (role.equals("Store Owner", ignoreCase = true)) {
                    val intent = Intent(this, StoreOwnerActivity::class.java)
                    intent.putExtra("EXTRA_USERNAME", username)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, CustomerDashboardActivity::class.java)
                    intent.putExtra("EXTRA_USERNAME", username)
                    startActivity(intent)
                }
                finish()
            } else {
                Toast.makeText(this, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }

        tvRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
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
}