package com.example.quizly.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.example.quizly.R
import com.example.quizly.models.QuestionModel
import com.example.quizly.viewmodels.CountDownViewModel
import com.example.quizly.viewmodels.QuestionViewModel
import java.util.*

class QuestionDetailScreenFragment : Fragment() {

    //Declare variables for views
    private var countdownTextView: TextView? = null
    private lateinit var radioButton1: RadioButton
    private lateinit var radioButton2: RadioButton
    private lateinit var radioButton3: RadioButton
    private lateinit var radioButton4: RadioButton
    private lateinit var bookmarkButton: ImageButton
    private lateinit var submitButton: Button
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button

    //Declare view models
    private val countDownViewModel: CountDownViewModel by activityViewModels()
    private val questionViewModel: QuestionViewModel by activityViewModels()

    //Declare other variables
    private var currentPosition: Int = 0
    private var numberOfQuestions: Int = 0
    private var isBookmarked: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_question_detail_screen, container, false)

        // Initialize views
        submitButton = view.findViewById(R.id.submit_button)
        prevButton = view.findViewById(R.id.previous_button)
        nextButton = view.findViewById(R.id.next_button)
        radioButton1 = view.findViewById(R.id.option1)
        radioButton2 = view.findViewById(R.id.option2)
        radioButton3 = view.findViewById(R.id.option3)
        radioButton4 = view.findViewById(R.id.option4)
        bookmarkButton = view.findViewById(R.id.add_or_remove_bookmark)
        countdownTextView = activity?.findViewById(R.id.tv_countdown)!!

        // Get the current question position
        currentPosition = arguments?.getInt(getString(R.string.positin_const), 0) ?: 0

        //Observe the countdown timer
        countDownViewModel.remainingTimeLiveData.observe(viewLifecycleOwner) { remainingTime ->
            if (remainingTime == 0L) {
                // If timer ends, navigate to the SummaryScreenFragment
                countdownTextView?.visibility = View.INVISIBLE
                val fragmentManager = activity?.supportFragmentManager
                fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                fragmentManager?.beginTransaction()
                    ?.replace(R.id.frame_layout, SummaryScreenFragment())?.commit()
            } else {
                // Update the countdown timer
                countdownTextView?.text = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    remainingTime / 1000 / 60,
                    (remainingTime / 1000) % 60
                )
            }
        }

        //Set up the Previous Button click listener
        prevButton.setOnClickListener {
            if (currentPosition > 0) {
                currentPosition--
                questionViewModel.getQuestionsLiveData()
                    .observe(viewLifecycleOwner) { questionList ->
                        if (questionList != null) {
                            displayQuestion(questionList[currentPosition])
                        }
                    }
            }
            prevButton.isEnabled = currentPosition != 0
            nextButton.isEnabled = currentPosition != numberOfQuestions - 1

        }

        //Set up the Next Button click listener
        nextButton.setOnClickListener {
            if (currentPosition < numberOfQuestions - 1) {
                currentPosition++
                questionViewModel.getQuestionsLiveData()
                    .observe(viewLifecycleOwner) { questionList ->
                        if (questionList != null) {
                            displayQuestion(questionList[currentPosition])
                        }
                    }
            }
            nextButton.isEnabled = currentPosition != numberOfQuestions - 1
            prevButton.isEnabled = currentPosition != 0
        }

        submitButton.setOnClickListener {

            // Create the AlertDialog
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(getString(R.string.alert_dialog_message))

            // Set up the Positive Button action
            builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
                countDownViewModel.stopCountdown()
                countdownTextView?.visibility = View.INVISIBLE

                // Launch the SummaryScreen Fragment
                val fragmentManager = activity?.supportFragmentManager
                fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                fragmentManager?.beginTransaction()
                    ?.replace(R.id.frame_layout, SummaryScreenFragment())?.commit()
            }

            // Set up the Negative Button action
            builder.setNegativeButton(getString(R.string.no)) { _, _ ->
                // Continue with the test
                countDownViewModel.startCountdown()
                countdownTextView?.visibility = View.VISIBLE
            }

            // Show the AlertDialog
            val alertDialog = builder.create()
            alertDialog.show()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe changes to the list of questions in the ViewModel
        questionViewModel.getQuestionsLiveData().observe(viewLifecycleOwner) { questionList ->
            if (questionList != null) {
                // Update the UI with the current question
                displayQuestion(questionList[currentPosition])
                numberOfQuestions = questionList.size
                prevButton.isEnabled = currentPosition != 0
                nextButton.isEnabled = currentPosition != numberOfQuestions - 1
            }
        }


    }

    private fun displayQuestion(question: QuestionModel) {
        // Clear the selected option if any
        view?.findViewById<RadioGroup>(R.id.options_radio_group)?.clearCheck()

        // Display the question text and options
        view?.findViewById<TextView>(R.id.question_text)?.text = question.question
        radioButton1.text = question.options?.get(0)
        radioButton2.text = question.options?.get(1)
        radioButton3.text = question.options?.get(2)
        radioButton4.text = question.options?.get(3)

        // Update the bookmark button based on the current question's bookmarked status
        isBookmarked = if (question.isBookmarked) {
            bookmarkButton.setImageResource(R.drawable.baseline_bookmark_added_24)
            true
        } else {
            bookmarkButton.setImageResource(R.drawable.baseline_bookmark_add_24)
            false
        }

        // Handle bookmark button clicks
        bookmarkButton.setOnClickListener {
            isBookmarked = if (isBookmarked) {
                bookmarkButton.setImageResource(R.drawable.baseline_bookmark_add_24)
                questionViewModel.updateBookmark(question.id, false)
                false
            } else {
                bookmarkButton.setImageResource(R.drawable.baseline_bookmark_added_24)
                questionViewModel.updateBookmark(question.id, true)
                true
            }
        }

        // Set up radio button listeners to update the selected option for the current question
        radioButton1.setOnCheckedChangeListener(null)
        radioButton2.setOnCheckedChangeListener(null)
        radioButton3.setOnCheckedChangeListener(null)
        radioButton4.setOnCheckedChangeListener(null)

        question.selectedOption?.let { selectedOption ->
            // Set the radio button corresponding to the current question's selected option
            when (selectedOption) {
                0 -> radioButton1.isChecked = true
                1 -> radioButton2.isChecked = true
                2 -> radioButton3.isChecked = true
                3 -> radioButton4.isChecked = true
            }
        }

        // Set up radio button listeners to update the selected option for the current question
        radioButton1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Update the selected option for the current question
                questionViewModel.updateSelectedOption(question.id, 0)
            }
        }
        radioButton2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Update the selected option for the current question
                questionViewModel.updateSelectedOption(question.id, 1)
            }
        }
        radioButton3.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Update the selected option for the current question
                questionViewModel.updateSelectedOption(question.id, 2)
            }
        }
        radioButton4.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Update the selected option for the current question
                questionViewModel.updateSelectedOption(question.id, 3)
            }
        }
    }
}
