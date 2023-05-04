package com.example.quizly.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizly.R
import com.example.quizly.models.QuestionModel

class QuestionsListAdapter(
    private val questionList: List<QuestionModel>, // list of QuestionModel objects to be displayed
    private val isBookmarkList: List<Boolean>, // list of Boolean values indicating whether each question is bookmarked
    private val isSelectedOptionList: List<Boolean> // list of Boolean values indicating whether each question has a selected option
) : RecyclerView.Adapter<QuestionsListAdapter.ViewHolder>() {

    // Interface for handling click events on the questions
    interface OnItemClickListener {
        fun onQuestionClicked(position: Int)
    }

    var onQuestionClickListener: OnItemClickListener? = null

    // Create a new view holder instance
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_question_view, parent, false)
        return ViewHolder(view)
    }

    // Bind data to the view holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = questionList[position]
        holder.questionName.text = question.question
        if (isBookmarkList[position]) {
            holder.bookmarkImageView.visibility =
                View.VISIBLE // show the bookmark icon if the question is bookmarked
        } else {
            holder.bookmarkImageView.visibility =
                View.INVISIBLE // hide the bookmark icon if the question is not bookmarked
        }
        if (isSelectedOptionList[position]) {
            holder.tickImageView.visibility =
                View.VISIBLE // show the tick icon if an option is selected for the question
        } else {
            holder.tickImageView.visibility =
                View.INVISIBLE // hide the tick icon if no option is selected for the question
        }
    }

    // Return the total number of items in the data set
    override fun getItemCount(): Int {
        return questionList.size
    }

    // Set a listener for click events on the questions
    fun setQuestionClickListener(onQuestionClickListener: OnItemClickListener) {
        this.onQuestionClickListener = onQuestionClickListener
    }

    // View holder class for holding references to the views in each list item
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questionName: TextView = itemView.findViewById(R.id.question_name)
        val bookmarkImageView: ImageView = itemView.findViewById(R.id.i_bookmark)
        val tickImageView: ImageView = itemView.findViewById(R.id.i_done)

        init {
            questionName.setOnClickListener {
                onQuestionClickListener?.onQuestionClicked(adapterPosition) // trigger the click event when the question is clicked
            }
        }
    }
}