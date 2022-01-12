package com.speedtracker.app.screens.mainscreen.statistics

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justwatter.app.helper.AppDataStoreImpl
import com.speedtracker.R
import com.speedtracker.helper.GenerallData
import com.speedtracker.model.AppDatabase
import com.speedtracker.model.Location
import com.speedtracker.model.OverallData
import com.speedtracker.model.TripInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    var appDataStoreImpl: AppDataStoreImpl
)  :ViewModel() {

    var overallStatisticsList:List<Statistic> = ArrayList()
    var tripStatisticsList:List<Statistic> = ArrayList()

    var trip:MutableLiveData<TripInfo?> = MutableLiveData(null)
    var overallData:OverallData = OverallData()

    fun initializeStatisticsData(context: Context) {
        viewModelScope.launch {
            appDataStoreImpl.getOverallData().collect { overallData ->
                if (overallData != null) {
                    overallStatisticsList = listOf(
                        Statistic(iconDrawable = R.drawable.ic_avgspeed,"Avg speed:", MutableLiveData((overallData.sumOfSpeeds/overallData.countOfUpdates).toString()), if (GenerallData.isMetric.value!!)  MutableLiveData(context.getString(
                            R.string.speed_units_metric)) else MutableLiveData(context.getString(R.string.speed_units_imperial))),
                        Statistic(iconDrawable = R.drawable.ic_topspeed,"Max speed:", MutableLiveData(overallData.maxSpeed.toString()), if (GenerallData.isMetric.value!!)  MutableLiveData(context.getString(
                            R.string.speed_units_metric)) else MutableLiveData(context.getString(R.string.speed_units_imperial))),
                        Statistic(iconDrawable = R.drawable.ic_distance,"Overall distance:", MutableLiveData(overallData.sumOfDistancesInM.toString()), if (GenerallData.isMetric.value!!)  MutableLiveData(context.getString(
                            R.string.measute_units_metric)) else MutableLiveData(context.getString(R.string.measute_units_imperial)))
                    )
                } else {
                    overallStatisticsList = listOf(
                        Statistic(iconDrawable = R.drawable.ic_avgspeed,"Avg speed:", MutableLiveData("0"), if (GenerallData.isMetric.value!!)  MutableLiveData(context.getString(
                            R.string.speed_units_metric)) else MutableLiveData(context.getString(R.string.speed_units_imperial))),
                        Statistic(iconDrawable = R.drawable.ic_topspeed,"Max speed:", MutableLiveData("0"), if (GenerallData.isMetric.value!!)  MutableLiveData(context.getString(
                            R.string.speed_units_metric)) else MutableLiveData(context.getString(R.string.speed_units_imperial))),
                        Statistic(iconDrawable = R.drawable.ic_distance,"Overall distance:", MutableLiveData("0"), if (GenerallData.isMetric.value!!)  MutableLiveData(context.getString(
                            R.string.measute_units_metric)) else MutableLiveData(context.getString(R.string.measute_units_imperial)))
                    )
                }
                appDataStoreImpl.getCurrentlyStartedTrip().collect { tripId ->
                    trip.value = TripInfo()
                    if (tripId != null && tripId > 0) {
    //                    var tripD = AppDatabase.getDatabase(context).tripDao().getTripDataById(tripId)
                        trip.value = AppDatabase.getDatabase(context).tripDao().getTripDataById(tripId).tripInfo
                        if (trip.value != null) {
                            if (trip.value!!.countOfUpdates > 0) {
                                tripStatisticsList = listOf(
                                    Statistic(iconDrawable = R.drawable.ic_avgspeed,"Avg speed:", MutableLiveData((trip.value!!.sumOfTripSpeed/trip.value!!.countOfUpdates).toString()), if (GenerallData.isMetric.value!!)  MutableLiveData(context.getString(
                                        R.string.speed_units_metric)) else MutableLiveData(context.getString(R.string.speed_units_imperial))),
                                    Statistic(iconDrawable = R.drawable.ic_topspeed,"Max speed:", MutableLiveData(trip.value!!.maxSpeed.toString()), if (GenerallData.isMetric.value!!)  MutableLiveData(context.getString(
                                        R.string.speed_units_metric)) else MutableLiveData(context.getString(R.string.speed_units_imperial))),
                                    Statistic(iconDrawable = R.drawable.ic_distance,"Trip distance:", MutableLiveData(trip.value!!.distance.toString()), if (GenerallData.isMetric.value!!)  MutableLiveData(context.getString(
                                        R.string.measute_units_metric)) else MutableLiveData(context.getString(R.string.measute_units_imperial))),
                                )
                            } else {
                                getEmptyTripStatisticData(context)
                            }
                        } else {
                            appDataStoreImpl.setCurrentlyStartedTrip(0)
                            getEmptyTripStatisticData(context)
                        }
                    } else {
                        getEmptyTripStatisticData(context)
                    }
                }
            }
        }
    }

    fun getEmptyTripStatisticData(context: Context) {
        tripStatisticsList = listOf(
            Statistic(
                iconDrawable = R.drawable.ic_avgspeed,
                "Avg speed:",
                MutableLiveData("0"),
                if (GenerallData.isMetric.value!!) MutableLiveData(
                    context.getString(
                        R.string.speed_units_metric
                    )
                ) else MutableLiveData(context.getString(R.string.speed_units_imperial))
            ),
            Statistic(
                iconDrawable = R.drawable.ic_topspeed,
                "Max speed:",
                MutableLiveData("0"),
                if (GenerallData.isMetric.value!!) MutableLiveData(
                    context.getString(
                        R.string.speed_units_metric
                    )
                ) else MutableLiveData(context.getString(R.string.speed_units_imperial))
            ),
            Statistic(
                iconDrawable = R.drawable.ic_distance,
                "Trip distance:",
                MutableLiveData("0"),
                if (GenerallData.isMetric.value!!) MutableLiveData(
                    context.getString(
                        R.string.measute_units_metric
                    )
                ) else MutableLiveData(context.getString(R.string.measute_units_imperial))
            ),
        )
    }

    fun startTrip(tripName: String,context: Context) {
        trip.value = TripInfo(tripName = tripName, tripStartDate = Calendar.getInstance().time.time)
        viewModelScope.launch {
            appDataStoreImpl.setCurrentlyStartedTrip(trip.value!!.tripId)
            AppDatabase.getDatabase(context).tripDao().insertTripInfo(trip.value!!)
        }
    }

    fun getAllTrips(context: Context){
        viewModelScope.launch {
            var allTrips = AppDatabase.getDatabase(context).tripDao().getAllTripData()
            Log.d("Ahoj","Ako sa mas")
        }
    }

    fun closeTrip(context: Context) {
        trip.value!!.tripEndDate = Calendar.getInstance().time.time
        viewModelScope.launch {
            appDataStoreImpl.setCurrentlyStartedTrip(0)
            AppDatabase.getDatabase(context).tripDao().updateTrip(trip.value!!)
            trip.value = null
        }
    }

    fun updateTrip(speed:Int,distanceToSave: Double,location: Location,context: Context) {
        trip.value!!.sumOfTripSpeed += speed
        trip.value!!.countOfUpdates++
        trip.value!!.distance += distanceToSave

        if (trip.value!!.maxSpeed < speed) {
            trip.value!!.maxSpeed = speed
        }

        tripStatisticsList.get(0).value.value = (trip.value!!.sumOfTripSpeed / trip.value!!.countOfUpdates).toString()
        tripStatisticsList.get(1).value.value = trip.value!!.maxSpeed.toString()
        tripStatisticsList.get(2).value.value = trip.value!!.distance.toString()

        viewModelScope.launch {
            AppDatabase.getDatabase(context = context).tripDao().updateTrip(trip.value!!)
            AppDatabase.getDatabase(context = context).tripDao().addLocation(location = location)
        }
    }

    fun updateOverallData(speed:Int,distanceToSave:Double) {
        overallData.countOfUpdates ++
        overallData.sumOfSpeeds += speed
        overallData.sumOfDistancesInM += distanceToSave

        if (overallData.maxSpeed < speed) {
            overallData.maxSpeed = speed
        }
        overallStatisticsList.get(0).value.value = (overallData.sumOfSpeeds / overallData.countOfUpdates).toString()
        overallStatisticsList.get(1).value.value = overallData.maxSpeed.toString()
        overallStatisticsList.get(2).value.value = overallData.sumOfDistancesInM.toString()

        viewModelScope.launch {
            appDataStoreImpl.setOverallData(overallData)
        }
    }

}

class Statistic (
    var iconDrawable:Int,
    var name:String,
    var value:MutableLiveData<String>,
    var units:MutableLiveData<String>
    )

