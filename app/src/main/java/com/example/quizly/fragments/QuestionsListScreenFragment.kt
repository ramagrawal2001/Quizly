package com.example.quizly.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizly.R
import com.example.quizly.adapters.QuestionsListAdapter
import com.example.quizly.models.QuestionModel
import com.example.quizly.viewmodels.CountDownViewModel
import com.example.quizly.viewmodels.QuestionViewModel
import java.util.*

class QuestionsListScreenFragment : Fragment(), QuestionsListAdapter.OnItemClickListener {

    private var countdownTextView: TextView? = null

    private var recyclerView: RecyclerView? = null

    private val questionsViewModel: QuestionViewModel by activityViewModels()
    private val countDownViewModel: CountDownViewModel by activityViewModels()

    //    private AlertDialog failureDialog;
    private var loadingDialog: ProgressDialog? = null
    private var errorMessage: Toast? = null
    private var activityMain: View? = null
    private lateinit var submitButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_questions_list_screen, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        countdownTextView = activity?.findViewById(R.id.tv_countdown)!!

        submitButton = view.findViewById(R.id.submit_button)

        setUpLiveData()
        activityMain = requireActivity().findViewById(android.R.id.content)

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
                    ?.replace(R.id.frame_layout, SummaryScreenFragment())
                    ?.commit()
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

    private fun setUpLiveData() {
        questionsViewModel.getQuestionsLiveData().observe(
            viewLifecycleOwner
        ) { questionModels ->
            questionModels?.let { handleQuestionList(it) }
            countDownViewModel.startCountdown()
        }
        questionsViewModel.getRequestStatusLiveData()
            .observe(viewLifecycleOwner) { requestStatus ->
                handleRequestStatus(requestStatus)
            }
        countDownViewModel.remainingTimeLiveData.observe(viewLifecycleOwner) { remainingTime ->
            if (remainingTime == 0L) {
                countdownTextView?.visibility = View.INVISIBLE
                val fragmentManager = activity?.supportFragmentManager
                fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                fragmentManager?.beginTransaction()
                    ?.replace(R.id.frame_layout, SummaryScreenFragment())
                    ?.commit()
            } else {
                countdownTextView?.text = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    remainingTime / 1000 / 60,
                    (remainingTime / 1000) % 60
                )
            }
        }
    }

    private fun handleRequestStatus(requestStatus: QuestionViewModel.RequestStatus) {
        when (requestStatus) {
            QuestionViewModel.RequestStatus.IN_PROGRESS -> showSpinner()
            QuestionViewModel.RequestStatus.SUCCEEDED -> hideSpinner()
            QuestionViewModel.RequestStatus.FAILED -> showError()
        }
    }

    private fun showSpinner() {
        if (loadingDialog == null) {
            loadingDialog = ProgressDialog(requireContext())
            loadingDialog?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            loadingDialog?.setTitle(getString(R.string.fetching_questions))
            loadingDialog?.setMessage(getString(R.string.please_wat))
            loadingDialog?.isIndeterminate = true
            loadingDialog?.setCanceledOnTouchOutside(false)
        }
        loadingDialog?.show()
    }


    private fun hideSpinner() {
        if (loadingDialog != null) {
            loadingDialog?.dismiss()
        }
    }

    private fun showError() {
        hideSpinner()
        if (errorMessage == null) {
            errorMessage =
                Toast.makeText(
                    requireContext(),
                    getString(R.string.list_unavailable),
                    Toast.LENGTH_SHORT
                )
        }
        errorMessage!!.show()
    }

    private fun cancelToast() {
        if (errorMessage != null) {
            errorMessage?.cancel()
        }
    }

    private fun handleQuestionList(questionModels: List<QuestionModel>) {
        val isBookmarkList = questionModels.map { it.isBookmarked }
        val isSelectedOption = questionModels.map { it.selectedOption != null }
        val adapter = QuestionsListAdapter(questionModels, isBookmarkList, isSelectedOption)
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
        adapter.setQuestionClickListener(this)
        recyclerView?.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        cancelToast()
    }

    override fun onQuestionClicked(position: Int) {
        val bundle = Bundle()
        bundle.putInt(getString(R.string.positin_const), position)

        val questionDetailFragment = QuestionDetailScreenFragment()
        questionDetailFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, questionDetailFragment)
            .addToBackStack(null)
            .commit()
    }

}
