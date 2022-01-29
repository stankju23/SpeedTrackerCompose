package com.justwatter.app.helper

import com.speedtracker.model.OverallData
import kotlinx.coroutines.flow.Flow

interface AppDataStore {

    fun getOverallData(): Flow<OverallData?>
    suspend fun setOverallData(overallData: OverallData)

    fun getCurrentlyStartedTrip():Flow<Long?>
    suspend fun setCurrentlyStartedTrip(identifier:Long)

    fun getIsMetric():Flow<Boolean?>
    suspend fun setIsMetric(isMetric:Boolean)

}
