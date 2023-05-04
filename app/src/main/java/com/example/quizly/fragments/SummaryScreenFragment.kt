package com.example.quizly.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.quizly.MainActivity
import com.example.quizly.R
import com.example.quizly.viewmodels.COUNTDOWN_TIME
import com.example.quizly.viewmodels.CountDownViewModel
import com.example.quizly.viewmodels.QuestionViewModel
import java.util.*


class SummaryScreenFragment : Fragment() {
    private lateinit var tvScore: TextView
    private lateinit var tvTimeTaken: TextView
    private lateinit var btnRestart: Button
    private lateinit var btnExit: Button

    private val questionsViewModel: QuestionViewModel by activityViewModels()
    private val countDownViewModel: CountDownViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_summary_screen, container, false)

        // Find views by ID
        tvScore = view.findViewById(R.id.tv_score)
        tvTimeTaken = view.findViewById(R.id.tv_time_taken)
        btnRestart = view.findViewById(R.id.btn_restart)
        btnExit = view.findViewById(R.id.btn_exit)

        val remainingTime = countDownViewModel.getRemainingTime()
        val elapsedTime = COUNTDOWN_TIME - remainingTime
        val minutes = elapsedTime / (1000 * 60)
        val seconds = (elapsedTime / 1000) % 60
        val elapsedTimeString = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

        tvTimeTaken.text = "Time taken:$elapsedTimeString"

        val score: Int = questionsViewModel.getScore()
        tvScore.text = "Your score: $score/10"


        // Set click listeners for restart and exit buttons
        btnRestart.setOnClickListener {
            // Clear all instances of the view holder
            questionsViewModel.clear()
            countDownViewModel.clear()

            // Navigate to the MainActivity containing the SetupScreenFragment
            val intent = Intent(activity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        btnExit.setOnClickListener {
            // Clear all instances of the view holder
            questionsViewModel.clear()
            countDownViewModel.clear()

            // Exit the application
            activity?.finish()
        }

        return view
    }
}