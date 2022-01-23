package com.speedtracker.app.screens.trips

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.speedtracker.model.AppDatabase
import com.speedtracker.model.Location
import com.speedtracker.model.TripData
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class TripViewModel : ViewModel() {


    var choosedTrip:MutableLiveData<TripData> = MutableLiveData()
    var tripList:MutableLiveData<ArrayList<TripData>> = MutableLiveData()

    fun getAddressFromLocation(location: Location, context: Context) :List<String>{
        var geocoder = Geocoder(context, Locale.getDefault())
        var addresses:List<Address>? = null
        try {
            addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                10
            )
        } catch (e: Exception) {
            Log.e("Geocoder error ", e.localizedMessage)
        }

        var addressList = ArrayList<String>()
        if (addresses != null && addresses.get(0).thoroughfare != null) {
            addressList.add(addresses.get(0).thoroughfare)
        }

        var locality: Address?
        if (addresses != null && addresses.get(0).featureName != null) {
            addressList.add(addresses.get(0).featureName)
            locality = addresses.firstOrNull { address -> address.locality != null }
            if (locality != null) {

                addressList.add(locality.locality)
                if (addressList.size == 2) {
                    addressList.add(0,locality.locality)
                }
            } else {
                addressList.add("Unknown")
            }
        }
        return addressList
    }

    suspend fun loadTrips(context:Context) {
        tripList.value = ArrayList(AppDatabase.getDatabase(context = context).tripDao().getAllTripData().reversed())
    }
}