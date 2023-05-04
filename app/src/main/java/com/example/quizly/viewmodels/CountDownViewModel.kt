package com.example.quizly.viewmodels

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

const val COUNTDOWN_TIME: Long = 10 * 60 * 1000

class CountDownViewModel : ViewModel() {
    companion object {

        private const val COUNTDOWN_INTERVAL: Long = 1000 // 1 second

    }

    val remainingTimeLiveData = MutableLiveData(COUNTDOWN_TIME)

    private var countDownTimer: CountDownTimer? = null

    init {
        remainingTimeLiveData.value = COUNTDOWN_TIME
    }

    fun startCountdown() {
        countDownTimer = object : CountDownTimer(
            remainingTimeLiveData.value ?: 0, COUNTDOWN_INTERVAL
        ) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTimeLiveData.value = millisUntilFinished
            }

            override fun onFinish() {
                remainingTimeLiveData.value = 0

            }
        }.start()
    }

    fun stopCountdown() {
        countDownTimer?.cancel()
    }

    fun clear() {
        stopCountdown()
        remainingTimeLiveData.value = COUNTDOWN_TIME
    }

    fun getRemainingTime(): Long {
        return remainingTimeLiveData.value ?: 0
    }
}