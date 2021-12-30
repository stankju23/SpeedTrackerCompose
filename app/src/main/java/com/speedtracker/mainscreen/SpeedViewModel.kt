package com.speedtracker.mainscreen

import android.animation.ValueAnimator
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SpeedViewModel:ViewModel() {

    var speed:MutableLiveData<Float> = MutableLiveData(0f)

    fun animate0To200And200To0() {
        val va = ValueAnimator.ofFloat(0f, 200f,0f)
        va.duration = 10000 //in millis
        va.addUpdateListener { animation -> speed.value = animation.animatedValue as Float }
        va.start()
    }
}