package com.example.quizly.models

data class QuestionModel(
    val correctOption: Int = 0,
    val question: String = "",
    val options: List<String>?,
    val id: Int = 0,
    var selectedOption: Int? = null,
    val isBookmarked: Boolean = false
)