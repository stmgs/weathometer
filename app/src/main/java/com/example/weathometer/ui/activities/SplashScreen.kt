package com.example.weathometer.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.weathometer.R
import com.example.weathometer.databinding.ActivitySplashScreenBinding


class SplashScreen : AppCompatActivity() {

    private lateinit var binding : ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val slideUp: Animation = AnimationUtils.loadAnimation(this,  R.anim.slide_up)
        binding.imgShovlLogo.startAnimation(slideUp)

        val slideDown: Animation = AnimationUtils.loadAnimation(this, R.anim.slide_down)
        binding.tvTitleSplash.startAnimation(slideDown)

        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }, 2000)

    }

}
