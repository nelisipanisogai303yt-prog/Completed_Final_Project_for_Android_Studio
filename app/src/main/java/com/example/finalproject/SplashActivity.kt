package com.example.finalproject

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val llBranding = findViewById<LinearLayout>(R.id.llSplashBranding)

        startBackgroundFloatingAnimations()

        llBranding.alpha = 0f
        llBranding.scaleX = 0.5f
        llBranding.scaleY = 0.5f

        val alphaAnim = ObjectAnimator.ofFloat(llBranding, "alpha", 0f, 1f)
        val scaleXAnim = ObjectAnimator.ofFloat(llBranding, "scaleX", 0.5f, 1f)
        val scaleYAnim = ObjectAnimator.ofFloat(llBranding, "scaleY", 0.5f, 1f)

        val entranceSet = AnimatorSet().apply {
            playTogether(alphaAnim, scaleXAnim, scaleYAnim)
            duration = 900L
            interpolator = OvershootInterpolator(1.2f)
        }
        entranceSet.start()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 2200L)
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