package com.speedtracker.app.screens.mainscreen.speed

import android.animation.ValueAnimator
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SpeedViewModel():ViewModel() {

    var speed:MutableLiveData<Int> = MutableLiveData(0)
    var altitude:MutableLiveData<Double> = MutableLiveData(0.0)
    var connectedSatelites:MutableLiveData<Int> = MutableLiveData(0)
    var allSatelites:MutableLiveData<Int> = MutableLiveData(0)
    var searchingForGPSLocation:MutableLiveData<Boolean> = MutableLiveData(true)

    var lastOverallLatitude = 0.0
    var lastOverallLongitude = 0.0
    var lastLatitude = 0.0
    var lastLongitude = 0.0
    var lastAltitude = 0.0
    var lastTime: Long = 0
    var actualLatitude = 0.0
    var actualLongitude = 0.0
    var actualAltitude = 0.0
    var actualTime: Long = 0

    fun animate0To200And200To0() {
        val va = ValueAnimator.ofInt(0, 200,0)
        va.duration = 10000 //in millis
        va.addUpdateListener { animation -> speed.value = animation.animatedValue as Int }
        va.start()
    }

    fun animateSpeed(fromSpeed:Int, toSpeed:Int) {
        val va = ValueAnimator.ofInt(fromSpeed,toSpeed)
        va.duration = 500 //in millis
        va.addUpdateListener { animation -> speed.value = animation.animatedValue as Int }
        va.start()
    }

    fun countCurrentDistance(): Double {
        if (lastLongitude != 0.0 && lastLatitude != 0.0) {
            var distance = getDistanceBetweenTwoLocations(
                lastLatitude,
                lastLongitude,
                actualLatitude,
                actualLongitude
            )
            Log.i("Calculated distance ","${(Math.round((distance) * 10.0) / 10.0)}m")
//            Toast.makeText(
//                this,
//                "Distance from last location = ${(Math.round((distance * 1000) * 10.0) / 10.0)}m",
//                Toast.LENGTH_SHORT
//            ).show()
            lastLatitude = actualLatitude
            lastLongitude = actualLongitude
            lastAltitude = actualAltitude
            lastTime = actualTime
            return distance.toDouble()
        } else {
            lastLatitude = actualLatitude
            lastLongitude = actualLongitude
            lastAltitude = actualAltitude
            lastTime = actualTime
        }
        return 0.0
    }

    fun getDistanceBetweenTwoLocations(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double
    ): Float {
        val loc1 = android.location.Location("")
        loc1.latitude = lat1
        loc1.longitude = lng1
        val loc2 = android.location.Location("")
        loc2.latitude = lat2
        loc2.longitude = lng2
        val distanceInMeters: Float = loc1.distanceTo(loc2)
        return distanceInMeters
    }
}