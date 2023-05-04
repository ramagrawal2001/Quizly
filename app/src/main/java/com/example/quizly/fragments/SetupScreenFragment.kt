package com.example.quizly.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.quizly.MainActivity
import com.example.quizly.R

class SetupScreenFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setup_screen, container, false)
        val startQuizButton = view.findViewById<Button>(R.id.b_start_quiz_button)

        startQuizButton.setOnClickListener {
            val parentActivity = activity as? MainActivity
            parentActivity?.showTimerTextView()
            val questionsListScreenFragment = QuestionsListScreenFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.frame_layout, questionsListScreenFragment)?.commit()
        }
        return view
    }
}