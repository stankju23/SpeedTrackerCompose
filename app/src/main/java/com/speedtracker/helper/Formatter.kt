package com.speedtracker.helper

import android.content.Context
import android.location.Geocoder
import android.util.Log
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
}