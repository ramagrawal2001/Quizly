package com.example.quizly.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.quizly.models.QuestionModel
import org.json.JSONException
import org.json.JSONObject

class QuestionViewModel(application: Application) : AndroidViewModel(application),
    Response.Listener<String>, Response.ErrorListener {
    companion object {
        private const val RESPONSE_ENTRY_KEY = "questions"
        private const val RESPONSE_Question_ID_KEY = "id"
        private const val RESPONSE_Question_NAME_KEY = "question"
        private const val RESPONSE_Question_OPTIONS_KEY = "options"
        private const val RESPONSE_Question_CORRECT_OPTION_KEY = "correct_option"
        private const val API =
            "https://gist.githubusercontent.com/ram-rsl/29ce1ada90eb011a2a8aa9ee24495c97/raw/2a02196bd1d8c9bc405d2942279597839f38e080/data.json"
    }

    private val questionsLiveData = MutableLiveData<List<QuestionModel>??>()
    private val requestStatusLiveData = MutableLiveData<RequestStatus>()

    private val queue: RequestQueue = Volley.newRequestQueue(application)

    init {
        requestStatusLiveData.postValue(RequestStatus.IN_PROGRESS)
        fetchQuestions()
    }

    fun getQuestionsLiveData(): MutableLiveData<List<QuestionModel>?> = questionsLiveData

    fun getRequestStatusLiveData(): LiveData<RequestStatus> = requestStatusLiveData

    override fun onResponse(response: String?) {
        try {
            response?.let {
                val questionModels = parseResponse(it)
                questionsLiveData.postValue(questionModels)
                requestStatusLiveData.postValue(RequestStatus.SUCCEEDED)
            } ?: run {
                requestStatusLiveData.postValue(RequestStatus.FAILED)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            requestStatusLiveData.postValue(RequestStatus.FAILED)
        }
    }

    override fun onErrorResponse(error: VolleyError?) {
        requestStatusLiveData.postValue(RequestStatus.FAILED)
    }

    fun clear() {
        questionsLiveData.value = null
        requestStatusLiveData.postValue(RequestStatus.IN_PROGRESS)
    }

    private fun fetchQuestions() {
        val stringRequest = StringRequest(
            Request.Method.GET, API, this, this
        )
        queue.add(stringRequest)
    }

    private fun parseResponse(response: String): List<QuestionModel> {
        val models = ArrayList<QuestionModel>()
        val res = JSONObject(response)
        val entries = res.optJSONArray(RESPONSE_ENTRY_KEY) ?: return models

        for (i in 0 until entries.length()) {
            val obj = entries[i] as JSONObject
            val id = obj.optInt(RESPONSE_Question_ID_KEY)
            val name = obj.optString(RESPONSE_Question_NAME_KEY)
            val optionsJsonArray = obj.optJSONArray(RESPONSE_Question_OPTIONS_KEY)
            val options = mutableListOf<String>()
            if (optionsJsonArray != null) {
                for (j in 0 until optionsJsonArray.length()) {
                    val option = optionsJsonArray.optString(j)
                    options.add(option)
                }
            }
            val correctOption = obj.optInt(RESPONSE_Question_CORRECT_OPTION_KEY)
            val correctOptionText = options[correctOption]
            // shuffle the options
            options.shuffle()
            val correctOptionIndex = options.indexOf(correctOptionText)
            val model = QuestionModel(correctOptionIndex, name, options, id, null, false)
            models.add(model)
        }
        models.shuffle()
        return models
    }

    fun updateSelectedOption(questionId: Int, selectedOption: Int) {
        val questionModels = questionsLiveData.value
        questionModels?.let {
            val updatedModels = it.map { model ->
                if (model.id == questionId) {
                    model.copy(selectedOption = selectedOption)
                } else {
                    model
                }
            }
            questionsLiveData.postValue(updatedModels)
        }
    }

    fun updateBookmark(questionId: Int, isBookmarked: Boolean) {
        val questionModels = questionsLiveData.value
        questionModels?.let {
            val updatedModels = it.map { model ->
                if (model.id == questionId) {
                    model.copy(isBookmarked = isBookmarked)
                } else {
                    model
                }
            }
            questionsLiveData.postValue(updatedModels)
        }
    }

    fun getScore(): Int {
        var score = 0
        val questionModels = questionsLiveData.value
        questionModels?.let {
            for (model in it) {
                if (model.selectedOption != null && model.selectedOption == model.correctOption) {
                    score++
                }
            }
        }
        return score
    }

    enum class RequestStatus {
        IN_PROGRESS, FAILED, SUCCEEDED
    }
}