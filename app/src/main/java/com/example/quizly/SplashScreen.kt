package com.example.quizly

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView

class SplashScreen : AppCompatActivity() {
    private var appname: TextView? = null
    var lottieAnimationView: LottieAnimationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        appname = findViewById(R.id.appname)
        lottieAnimationView = findViewById(R.id.lottie)
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1500)

    }


}