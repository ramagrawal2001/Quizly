package com.example.quizly

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.quizly.fragments.SetupScreenFragment

class MainActivity : AppCompatActivity() {
    private lateinit var timerTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerTextView = findViewById(R.id.tv_countdown)
        val setupScreenFragment = SetupScreenFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.frame_layout, setupScreenFragment)
            .commit()
    }

    fun showTimerTextView() {
        timerTextView.visibility = View.VISIBLE
    }
}