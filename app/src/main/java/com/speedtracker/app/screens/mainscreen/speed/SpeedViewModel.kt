package com.speedtracker.app.screens.mainscreen.speed

import android.animation.ValueAnimator
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SpeedViewModel():ViewModel() {

    var speed:MutableLiveData<Float> = MutableLiveData(0f)
    var altitude:MutableLiveData<Float> = MutableLiveData(0f)
    var connectedSatelites:MutableLiveData<Int> = MutableLiveData(0)
    var allSatelites:MutableLiveData<Int> = MutableLiveData(0)
    var searchingForGPSLocation:MutableLiveData<Boolean> = MutableLiveData(true)

    fun animate0To200And200To0() {
        val va = ValueAnimator.ofFloat(0f, 200f,0f)
        va.duration = 10000 //in millis
        va.addUpdateListener { animation -> speed.value = animation.animatedValue as Float }
        va.start()
    }
}