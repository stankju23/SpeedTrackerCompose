package com.speedtracker.app.screens.mainscreen.statistics

import android.content.Context
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

    var trip:TripInfo? = null
    var overallData:OverallData = OverallData()

    suspend fun initializeStatisticsData(context: Context) {

        appDataStoreImpl.getOverallData().collect { overallData ->
            if (overallData != null) {
                overallStatisticsList = listOf(
                    Statistic("Avg speed:", MutableLiveData(overallData.sumOfSpeeds/overallData.countOfUpdates), if (GenerallData.isMetric.value!!)  MutableLiveData(context.getString(
                        R.string.speed_units_metric)) else MutableLiveData(context.getString(R.string.speed_units_imperial))),
                    Statistic("Max speed:", MutableLiveData(overallData.maxSpeed), if (GenerallData.isMetric.value!!)  MutableLiveData(context.getString(
                        R.string.speed_units_metric)) else MutableLiveData(context.getString(R.string.speed_units_imperial))),
                    Statistic("Overall distance:", MutableLiveData(overallData.sumOfDistancesInM), if (GenerallData.isMetric.value!!)  MutableLiveData(context.getString(
                        R.string.measute_units_metric)) else MutableLiveData(context.getString(R.string.measute_units_imperial)))
                )
            } else {
                overallStatisticsList = listOf(
                    Statistic("Avg speed:", MutableLiveData(0), if (GenerallData.isMetric.value!!)  MutableLiveData(context.getString(
                        R.string.speed_units_metric)) else MutableLiveData(context.getString(R.string.speed_units_imperial))),
                    Statistic("Max speed:", MutableLiveData(0), if (GenerallData.isMetric.value!!)  MutableLiveData(context.getString(
                        R.string.speed_units_metric)) else MutableLiveData(context.getString(R.string.speed_units_imperial))),
                    Statistic("Overall distance:", MutableLiveData(0), if (GenerallData.isMetric.value!!)  MutableLiveData(context.getString(
                        R.string.measute_units_metric)) else MutableLiveData(context.getString(R.string.measute_units_imperial)))
                )
            }
            tripStatisticsList = listOf(
                Statistic("Avg speed:", MutableLiveData(0), if (GenerallData.isMetric.value!!)  MutableLiveData(context.getString(
                    R.string.speed_units_metric)) else MutableLiveData(context.getString(R.string.speed_units_imperial))),
                Statistic("Max speed:", MutableLiveData(0), if (GenerallData.isMetric.value!!)  MutableLiveData(context.getString(
                    R.string.speed_units_metric)) else MutableLiveData(context.getString(R.string.speed_units_imperial))),
                Statistic("Trip distance:", MutableLiveData(0), if (GenerallData.isMetric.value!!)  MutableLiveData(context.getString(
                    R.string.measute_units_metric)) else MutableLiveData(context.getString(R.string.measute_units_imperial))),
                Statistic("Trip avg altitude:", MutableLiveData(0), if (GenerallData.isMetric.value!!)  MutableLiveData(context.getString(
                    R.string.altitudeI_units_metric)) else MutableLiveData(context.getString(R.string.altitudeI_units_imperial)))
            )
        }
    }

    fun startTrip(tripName: String,context: Context) {
        trip = TripInfo(tripName = tripName, tripStartDate = Calendar.getInstance().time.time)
        viewModelScope.launch {
            appDataStoreImpl.setCurrentlyStartedTrip(trip!!.tripId)
            AppDatabase.getDatabase(context).tripDao().insertTripInfo(trip!!)
        }
    }

    fun updateTrip(speed:Int,distanceToSave: Double,location: Location,context: Context) {
        trip!!.sumOfTripSpeed += speed
        trip!!.countOfUpdates++
        trip!!.distance += distanceToSave
        viewModelScope.launch {
            AppDatabase.getDatabase(context = context).tripDao().updateTrip(trip!!)
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
        overallStatisticsList.get(0).value.value = overallData.sumOfSpeeds / overallData.countOfUpdates
        overallStatisticsList.get(1).value.value = overallData.maxSpeed
        overallStatisticsList.get(2).value.value = overallData.sumOfDistancesInM

        viewModelScope.launch {
            appDataStoreImpl.setOverallData(overallData)
        }
    }

}

class Statistic (
    var name:String,
    var value:MutableLiveData<Any>,
    var units:MutableLiveData<String>
    )

