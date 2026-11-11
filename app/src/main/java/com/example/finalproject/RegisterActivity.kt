package com.example.finalproject

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = DatabaseHelper(this)

        val etRegUsername = findViewById<EditText>(R.id.etRegUsername)
        val etRegPassword = findViewById<EditText>(R.id.etRegPassword)
        val rbStoreOwner = findViewById<RadioButton>(R.id.rbStoreOwner)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)

        startBackgroundFloatingAnimations()

        btnRegister.setOnClickListener {
            val username = etRegUsername.text.toString().trim()
            val password = etRegPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dbHelper.checkUsernameExists(username)) {
                Toast.makeText(this, "Username already exists. Please choose another.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedRole = if (rbStoreOwner.isChecked) "Store Owner" else "Customer"

            val success = dbHelper.registerUser(username, password, selectedRole)

            if (success) {
                Toast.makeText(this, "Registration Successful! Please Log In.", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Registration Failed. Try again.", Toast.LENGTH_SHORT).show()
            }
        }

        tvLoginLink.setOnClickListener {
            finish()
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