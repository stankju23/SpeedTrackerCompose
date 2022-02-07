package com.speedtracker.helper

import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.speedtracker.model.Location
import java.text.SimpleDateFormat
import java.util.*

object Formatter {

    fun formatDate(date:Long):String {
        return SimpleDateFormat("dd.MM.yyyy HH:mm").format(Date(date))
    }

    fun getCityFromLocation(location: Location, context: Context) : String? {
        try {
            var geocoder = Geocoder(context, Locale.getDefault())
            var startAddresses = geocoder.getFromLocation(location.latitude, location.longitude, 10)
            var locality = startAddresses.firstOrNull { address -> address.locality != null }
            if (locality != null) {
                return locality.locality
            } else {
                return null
            }
        } catch (e:Exception) {
            Log.e("Address from location", e.message.toString())
            return "Address not available"
        }
    }

    fun calculateTimeBetweenDates(startDate:Date, endDate:Date):Long {
        return (endDate.time - startDate.time)
    }

    fun formatTimeFromLong(different:Long):String {

        var formatedString = ""

        if (different != 0L) {
            println("different : $different")

            var newDifferent = different
            val secondsInMilli: Long = 1000
            val minutesInMilli = secondsInMilli * 60
            val hoursInMilli = minutesInMilli * 60
            val daysInMilli = hoursInMilli * 24

            val elapsedDays = newDifferent / daysInMilli
            newDifferent = newDifferent % daysInMilli

            val elapsedHours = newDifferent / hoursInMilli
            newDifferent = newDifferent % hoursInMilli

            val elapsedMinutes = newDifferent / minutesInMilli
            newDifferent = newDifferent % minutesInMilli

            val elapsedSeconds = newDifferent / secondsInMilli
            newDifferent = newDifferent % secondsInMilli

//        val elapsedSeconds = different / secondsInMilli

            if (elapsedDays != 0L) {
                formatedString += "${elapsedDays}d"
            }
            if (elapsedHours == 0L) {
                formatedString += "0:"
            } else {
                formatedString += "${elapsedHours}:"
            }

            if (elapsedMinutes == 0L) {
                formatedString += "00"
            } else {
                if (elapsedMinutes < 10) {
                    formatedString += "0${elapsedMinutes}"
                } else {
                    formatedString += "${elapsedMinutes}"
                }
            }
            if (elapsedSeconds == 0L) {
                formatedString += ":00"
            }  else {
                if (elapsedSeconds < 10) {
                    formatedString += ":0${elapsedSeconds}"
                } else {
                    formatedString += ":${elapsedSeconds}"
                }
            }
        } else {
            formatedString = "0:00:00"
        }


        return formatedString
    }

    fun calculateTripTime(startDate:Date, endDate:Date) : String {
        var different = (endDate.time - startDate.time)
        return formatTimeFromLong(different)
    }


    fun calculateTripDistance(locations:List<LatLng>):Double {
        var distance = 0.0f
        if (locations.size > 1) {
            for (i in 0..locations.size - 1) {
                if (i < locations.size - 1) {
                    distance += getDistanceBetweenTwoLocations(locations[i].latitude, locations[i].longitude,locations[i + 1].latitude, locations[i + 1].longitude)
                }
            }
        } else {
            distance = 0.0f
        }
        return Math.round(distance * 10.0) / 10.0
    }

    fun calculateTripDistanceFromLocationList(locations:List<Location>):Double {
        var distance = 0.0f
        if (locations.size > 1) {
            for (i in 0..locations.size - 1) {
                if (i < locations.size - 1) {
                    distance += getDistanceBetweenTwoLocations(locations[i].latitude, locations[i].longitude,locations[i + 1].latitude, locations[i + 1].longitude)
                }
            }
        } else {
            distance = 0.0f
        }
        return Math.round(distance * 10.0) / 10.0
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