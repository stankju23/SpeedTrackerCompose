package com.justwatter.app.helper

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.speedtracker.model.OverallData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val DATA_STORE_NAME = "overallData"
private val Context.overallDataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)

class AppDataStoreImpl @Inject constructor(@ApplicationContext val context: Context) : AppDataStore {

    override fun getOverallData(): Flow<OverallData?> = context.overallDataStore.data.map { overallDataPref ->
        Log.d("Get OverallData","Called")
        var overallData:OverallData? = null
        if (overallDataPref[PreferencesKeys.OVERALL_DATA_SUM_OF_SPEEDS] != null &&
            overallDataPref[PreferencesKeys.OVERALL_DATA_MAX_SPEED] != null &&
            overallDataPref[PreferencesKeys.OVERALL_DATA_COUNT_OF_UPDATES] != null &&
            overallDataPref[PreferencesKeys.OVERALL_DATA_SUM_OF_DISTANCES] != null) {
            overallData = OverallData(
                sumOfSpeeds = overallDataPref[PreferencesKeys.OVERALL_DATA_SUM_OF_SPEEDS] ?: 0,
                maxSpeed = overallDataPref[PreferencesKeys.OVERALL_DATA_MAX_SPEED] ?: 0,
                countOfUpdates = overallDataPref[PreferencesKeys.OVERALL_DATA_COUNT_OF_UPDATES] ?: 0,
                sumOfDistancesInM = overallDataPref[PreferencesKeys.OVERALL_DATA_SUM_OF_DISTANCES]?:0.0
            )
        } else {
            overallData = null
        }
        overallData
    }


    override suspend fun setOverallData(overallData: OverallData) {
        Log.d("Set Overall Data ","Called")
        context.overallDataStore.edit { dataStore ->
            dataStore[PreferencesKeys.OVERALL_DATA_SUM_OF_SPEEDS] = overallData.sumOfSpeeds ?: 0
            dataStore[PreferencesKeys.OVERALL_DATA_MAX_SPEED] = overallData.maxSpeed ?: 0
            dataStore[PreferencesKeys.OVERALL_DATA_COUNT_OF_UPDATES] = overallData.countOfUpdates ?: 0
            dataStore[PreferencesKeys.OVERALL_DATA_SUM_OF_DISTANCES] = overallData.sumOfDistancesInM ?: 0.0
        }
    }

    override fun getCurrentlyStartedTrip(): Flow<Long?> = context.overallDataStore.data.map { tripDataPref ->
        Log.d("Get Trip identifier","Called")
        var currentlyStartedTripIdentifier:Long? = null
        if (tripDataPref[PreferencesKeys.OVERALL_DATA_SUM_OF_SPEEDS] != null) {
            currentlyStartedTripIdentifier = tripDataPref[PreferencesKeys.CURRENTLY_STARTED_TRIP]
        }
        currentlyStartedTripIdentifier
    }

    override suspend fun setCurrentlyStartedTrip(identifier: Long) {
        context.overallDataStore.edit { dataStore ->
            dataStore[PreferencesKeys.CURRENTLY_STARTED_TRIP] = identifier
        }
    }

}

private object PreferencesKeys {
    val OVERALL_DATA_SUM_OF_SPEEDS = intPreferencesKey("sum_of_speeds")
    val OVERALL_DATA_COUNT_OF_UPDATES = intPreferencesKey("count_of_updates")
    val OVERALL_DATA_MAX_SPEED = intPreferencesKey("max_speed")
    val OVERALL_DATA_SUM_OF_DISTANCES = doublePreferencesKey("sum_of_distances_in_m")


    val CURRENTLY_STARTED_TRIP = longPreferencesKey("currently_started_trip")
}
