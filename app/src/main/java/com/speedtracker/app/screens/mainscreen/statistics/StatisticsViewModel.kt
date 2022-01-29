package com.speedtracker.app.screens.mainscreen.statistics

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justwatter.app.helper.AppDataStoreImpl
import com.speedtracker.DrawerValue
import com.speedtracker.R
import com.speedtracker.app.screens.mainscreen.speed.SpeedViewModel
import com.speedtracker.app.screens.settings.SettingsViewModel
import com.speedtracker.helper.Constants
import com.speedtracker.helper.GenerallData
import com.speedtracker.model.AppDatabase
import com.speedtracker.model.Location
import com.speedtracker.model.OverallData
import com.speedtracker.model.TripInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    var appDataStoreImpl: AppDataStoreImpl
)  :ViewModel() {

    var overallStatisticsList:List<Statistic> by mutableStateOf(listOf())
    var tripStatisticsList:List<Statistic> by mutableStateOf(listOf())

    var trip:MutableLiveData<TripInfo?> = MutableLiveData(null)
    var overallData:OverallData = OverallData()

    fun initializeStatisticsData(context: Context,settingsViewModel: SettingsViewModel) {

        Log.i("Initialize data", "Called")
        settingsViewModel.initializeIsMetricSetting(context = context)
        overallStatisticsList = listOf(
            Statistic(iconDrawable = R.drawable.ic_avgspeed,"Avg speed:", "0", if (GenerallData.isMetric.value!!)  context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial)),
            Statistic(iconDrawable = R.drawable.ic_topspeed,"Max speed:", "0", if (GenerallData.isMetric.value!!)  context.getString(
                R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial)),
            Statistic(iconDrawable = R.drawable.ic_distance,"Overall distance:", "0.0", if (GenerallData.isMetric.value!!)  context.getString(
                R.string.measute_units_metric) else context.getString(R.string.measute_units_imperial))
        )
        getEmptyTripStatisticData(context)

        viewModelScope.launch {
            appDataStoreImpl.getOverallData().collect { overallData ->
                if (overallData != null) {
                    this@StatisticsViewModel.overallData = overallData
                    overallStatisticsList = mutableListOf(
                        Statistic(iconDrawable = R.drawable.ic_avgspeed, "Avg speed:", if (GenerallData.isMetric.value!!) "${(overallData.sumOfSpeeds / overallData.countOfUpdates * Constants.msToKmh).toInt()}" else "${(overallData.sumOfSpeeds / overallData.countOfUpdates * Constants.msToMph).toInt()}",if(GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial)),
                        Statistic(iconDrawable = R.drawable.ic_topspeed, "Max speed:", "${if(GenerallData.isMetric.value!!) (overallData.maxSpeed * Constants.msToKmh).toInt() else (overallData.maxSpeed * Constants.msToMph).toInt()}",if(GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial)),
                        Statistic(iconDrawable = R.drawable.ic_distance, "Overall distance:", (Math.round((overallData.sumOfDistancesInM/ (if (GenerallData.isMetric.value!!) Constants.mToKm else Constants.mToMil)) * 10.0) / 10.0).toString(), if(GenerallData.isMetric.value!!) context.getString(R.string.measute_units_metric) else context.getString(R.string.measute_units_imperial))
                    )
//                    overallStatisticsList = overallStatisticsList.toMutableList().also {
//                        Log.i("View model overall scope1", "Again called")
//                        it[0] = Statistic(iconDrawable = R.drawable.ic_avgspeed, "Avg speed:", (overallData.sumOfSpeeds / overallData.countOfUpdates).toString(), if (GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial))
//                        it[1] =  Statistic(iconDrawable = R.drawable.ic_topspeed, "Max speed:", overallData.maxSpeed.toString(), if (GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial))
//                        it[2] = Statistic(iconDrawable = R.drawable.ic_distance, "Overall distance:", overallData.sumOfDistancesInM.toString(), if (GenerallData.isMetric.value!!) context.getString(R.string.measute_units_metric) else context.getString(R.string.measute_units_imperial))
//                    }
                } else {
                    overallStatisticsList = mutableListOf(
                        Statistic(iconDrawable = R.drawable.ic_avgspeed, "Avg speed:", "0", if (GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial)),
                        Statistic(iconDrawable = R.drawable.ic_topspeed, "Max speed:", "0", if (GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial)),
                        Statistic(iconDrawable = R.drawable.ic_distance, "Overall distance:", "0.0", if (GenerallData.isMetric.value!!) context.getString(R.string.measute_units_metric) else context.getString(R.string.measute_units_imperial))
                    )
//                    overallStatisticsList = overallStatisticsList.toMutableList().also {
//                        Log.i("View model overall scope2", "Again called")
//                        it[0] = Statistic(iconDrawable = R.drawable.ic_avgspeed, "Avg speed:", "0", if (GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial))
//                        it[1] =  Statistic(iconDrawable = R.drawable.ic_topspeed, "Max speed:", "0", if (GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial))
//                        it[2] = Statistic(iconDrawable = R.drawable.ic_distance, "Overall distance:", "0", if (GenerallData.isMetric.value!!) context.getString(R.string.measute_units_metric) else context.getString(R.string.measute_units_imperial))
//                    }
                }
                this.cancel()
            }
        }

        viewModelScope.launch {
            appDataStoreImpl.getCurrentlyStartedTrip().collect { tripId ->
                if (tripId != null && tripId > 0) {
                    trip.value = AppDatabase.getDatabase(context).tripDao().getTripDataById(tripId).tripInfo
                    if (trip.value != null) {
                        if (trip.value!!.countOfUpdates > 0) {
                            tripStatisticsList = mutableListOf(
                                Statistic(iconDrawable = R.drawable.ic_avgspeed, "Avg speed:", if (GenerallData.isMetric.value!!) "${(trip.value!!.sumOfTripSpeed /trip.value!!.countOfUpdates * Constants.msToKmh).toInt()}" else "${(trip.value!!.sumOfTripSpeed / trip.value!!.countOfUpdates * Constants.msToMph).toInt()}",if(GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial)),
                                Statistic(iconDrawable = R.drawable.ic_topspeed, "Max speed:", "${if(GenerallData.isMetric.value!!) (trip.value!!.maxSpeed * Constants.msToKmh).toInt() else (trip.value!!.maxSpeed * Constants.msToMph).toInt()}",if(GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial)),
                                Statistic(iconDrawable = R.drawable.ic_distance, "Trip distance:", (Math.round((trip.value!!.distance/ (if (GenerallData.isMetric.value!!) Constants.mToKm else Constants.mToMil)) * 10.0) / 10.0).toString(), if(GenerallData.isMetric.value!!) context.getString(R.string.measute_units_metric) else context.getString(R.string.measute_units_imperial))
                            )
//                            tripStatisticsList = tripStatisticsList.toMutableList().also {
//                                Log.i("View model statistics scope1", "Again called")
//                                it[0] = Statistic(iconDrawable = R.drawable.ic_avgspeed, "Avg speed:", (trip.value!!.sumOfTripSpeed / trip.value!!.countOfUpdates).toString(), if (GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial))
//                                it[1] =  Statistic(iconDrawable = R.drawable.ic_topspeed, "Max speed:", trip.value!!.maxSpeed.toString(), if (GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial))
//                                it[2] = Statistic(iconDrawable = R.drawable.ic_distance, "Trip distance:", trip.value!!.distance.toString(), if (GenerallData.isMetric.value!!) context.getString(R.string.measute_units_metric) else context.getString(R.string.measute_units_imperial))
//                            }
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
                this.cancel()
            }
        }
    }

    fun getEmptyTripStatisticData(context: Context) {
        if (tripStatisticsList.size != 0) {
            tripStatisticsList = tripStatisticsList.toMutableList().also {
                Log.i("View model statistics scope2", "Again called")
                it[0] = Statistic(iconDrawable = R.drawable.ic_avgspeed, "Avg speed:", "0", if(GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial))
                it[1] =  Statistic(iconDrawable = R.drawable.ic_topspeed, "Max speed:", "0", if(GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial))
                it[2] = Statistic(iconDrawable = R.drawable.ic_distance, "Trip distance:", "0.0", if(GenerallData.isMetric.value!!) context.getString(R.string.measute_units_metric) else context.getString(R.string.measute_units_imperial))
            }
        } else {
              tripStatisticsList = listOf(
                  Statistic(iconDrawable = R.drawable.ic_avgspeed, "Avg speed:", "0", if(GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial)),
                  Statistic(iconDrawable = R.drawable.ic_topspeed, "Max speed:", "0", if(GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial)),
                  Statistic(iconDrawable = R.drawable.ic_distance, "Trip distance:", "0.0",if(GenerallData.isMetric.value!!) context.getString(R.string.measute_units_metric) else context.getString(R.string.measute_units_imperial))
              )
        }

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
            getEmptyTripStatisticData(context = context)
        }
    }

    fun updateTrip(speed:Int,distanceToSave: Double,location: Location,context: Context) {
        trip.value!!.sumOfTripSpeed += speed
        trip.value!!.countOfUpdates++
        trip.value!!.distance += distanceToSave

        if (trip.value!!.maxSpeed < speed) {
            trip.value!!.maxSpeed = speed
        }

        tripStatisticsList = tripStatisticsList.toMutableList().also {
            Log.i("View model statistics scope2", "Again called")
            it[0] = Statistic(iconDrawable = R.drawable.ic_avgspeed, "Avg speed:",  if (GenerallData.isMetric.value!!) "${(trip.value!!.sumOfTripSpeed /trip.value!!.countOfUpdates * Constants.msToKmh).toInt()}" else "${(trip.value!!.sumOfTripSpeed / trip.value!!.countOfUpdates * Constants.msToMph).toInt()}",if(GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial))
            it[1] =  Statistic(iconDrawable = R.drawable.ic_topspeed, "Max speed:", "${if(GenerallData.isMetric.value!!) (trip.value!!.maxSpeed * Constants.msToKmh).toInt() else (trip.value!!.maxSpeed * Constants.msToMph).toInt()}",if(GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial))
            it[2] = Statistic(iconDrawable = R.drawable.ic_distance, "Trip distance:", (Math.round((trip.value!!.distance/ (if (GenerallData.isMetric.value!!) Constants.mToKm else Constants.mToMil)) * 10.0) / 10.0).toString(), if(GenerallData.isMetric.value!!) context.getString(R.string.measute_units_metric) else context.getString(R.string.measute_units_imperial))
        }

        viewModelScope.launch {
            AppDatabase.getDatabase(context = context).tripDao().updateTrip(trip.value!!)
            AppDatabase.getDatabase(context = context).tripDao().addLocation(location = location)
        }
    }

    fun updateTripUnits(context: Context) {
        if (trip.value == null || trip.value!!.countOfUpdates == 0) {
            getEmptyTripStatisticData(context = context)
        } else {
            tripStatisticsList = tripStatisticsList.toMutableList().also {
                Log.i("View model statistics scope2", "Again called")
                it[0] = Statistic(iconDrawable = R.drawable.ic_avgspeed, "Avg speed:",  if (GenerallData.isMetric.value!!) "${(trip.value!!.sumOfTripSpeed /trip.value!!.countOfUpdates * Constants.msToKmh).toInt()}" else "${(trip.value!!.sumOfTripSpeed / trip.value!!.countOfUpdates * Constants.msToMph).toInt()}",if(GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial))
                it[1] =  Statistic(iconDrawable = R.drawable.ic_topspeed, "Max speed:", "${if(GenerallData.isMetric.value!!) (trip.value!!.maxSpeed * Constants.msToKmh).toInt() else (trip.value!!.maxSpeed * Constants.msToMph).toInt()}",if(GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial))
                it[2] = Statistic(iconDrawable = R.drawable.ic_distance, "Trip distance:", (Math.round((trip.value!!.distance/ (if (GenerallData.isMetric.value!!) Constants.mToKm else Constants.mToMil)) * 10.0) / 10.0).toString(), if(GenerallData.isMetric.value!!) context.getString(R.string.measute_units_metric) else context.getString(R.string.measute_units_imperial))

            }
        }

    }

    fun updateOverallDataUnits(context: Context) {
        if (overallData.countOfUpdates == 0) {
            overallStatisticsList = overallStatisticsList.toMutableList().also {
                Log.i("View model overall scope4", "Again called")
                it[0] = Statistic(iconDrawable = R.drawable.ic_avgspeed, "Avg speed:", "0", if(GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial))
                it[1] =  Statistic(iconDrawable = R.drawable.ic_topspeed, "Max speed:", "0", if(GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial))
                it[2] = Statistic(iconDrawable = R.drawable.ic_distance, "Trip distance:", "0.0", if(GenerallData.isMetric.value!!) context.getString(R.string.measute_units_metric) else context.getString(R.string.measute_units_imperial))
            }
        } else {
            overallStatisticsList = overallStatisticsList.toMutableList().also {
                Log.i("View model overall scope4", "Again called")
                it[0] = Statistic(iconDrawable = R.drawable.ic_avgspeed, "Avg speed:",  if (GenerallData.isMetric.value!!) "${(overallData.sumOfSpeeds / overallData.countOfUpdates * Constants.msToKmh).toInt()}" else "${(overallData.sumOfSpeeds / overallData.countOfUpdates * Constants.msToMph).toInt()}",if(GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial))
                it[1] =  Statistic(iconDrawable = R.drawable.ic_topspeed, "Max speed:", "${if(GenerallData.isMetric.value!!) (overallData.maxSpeed * Constants.msToKmh).toInt() else (overallData.maxSpeed * Constants.msToMph).toInt()}",if(GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial))
                it[2] = Statistic(iconDrawable = R.drawable.ic_distance, "Trip distance:", (Math.round((overallData.sumOfDistancesInM/ (if (GenerallData.isMetric.value!!) Constants.mToKm else Constants.mToMil)) * 10.0) / 10.0).toString(), if(GenerallData.isMetric.value!!) context.getString(R.string.measute_units_metric) else context.getString(R.string.measute_units_imperial))
            }
        }

    }

    fun updateOverallData(speed:Int,distanceToSave:Double,context: Context) {
        overallData.countOfUpdates ++
        overallData.sumOfSpeeds += speed
        overallData.sumOfDistancesInM += distanceToSave

        if (overallData.maxSpeed < speed) {
            overallData.maxSpeed = speed
        }

        overallStatisticsList = overallStatisticsList.toMutableList().also {
            Log.i("View model overall scope3", "Again called")
            it[0] = Statistic(iconDrawable = R.drawable.ic_avgspeed, "Avg speed:",  if (GenerallData.isMetric.value!!) "${(overallData.sumOfSpeeds /overallData.countOfUpdates * Constants.msToKmh).toInt()}" else "${(overallData.sumOfSpeeds / overallData.countOfUpdates * Constants.msToMph).toInt()}",if(GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial))
            it[1] =  Statistic(iconDrawable = R.drawable.ic_topspeed, "Max speed:", "${if(GenerallData.isMetric.value!!) (overallData.maxSpeed * Constants.msToKmh).toInt() else (overallData.maxSpeed * Constants.msToMph).toInt()}",if(GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial))
            it[2] = Statistic(iconDrawable = R.drawable.ic_distance, "Trip distance:", (Math.round((overallData.sumOfDistancesInM/ (if (GenerallData.isMetric.value!!) Constants.mToKm else Constants.mToMil)) * 10.0) / 10.0).toString(), if(GenerallData.isMetric.value!!) context.getString(R.string.measute_units_metric) else context.getString(R.string.measute_units_imperial))
        }
//        overallStatisticsList.get(0).value.value = (overallData.sumOfSpeeds / overallData.countOfUpdates).toString()
//        overallStatisticsList.get(1).value.value = overallData.maxSpeed.toString()
//        overallStatisticsList.get(2).value.value = overallData.sumOfDistancesInM.toString()

        viewModelScope.launch {
            appDataStoreImpl.setOverallData(overallData)
        }
    }

}

class Statistic (
    var iconDrawable:Int,
    var name:String,
    var value:String,
    var units:String
    )

