package com.speedtracker.app.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justwatter.app.helper.AppDataStoreImpl
import com.speedtracker.app.screens.mainscreen.statistics.StatisticsViewModel
import com.speedtracker.helper.GenerallData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(var appDataStoreImpl: AppDataStoreImpl):ViewModel() {

    fun updateIsMetricSetting(isMetric:Boolean, statisticsViewModel: StatisticsViewModel,context: Context) {
        viewModelScope.launch {
            appDataStoreImpl.setIsMetric(isMetric)
        }
        statisticsViewModel.updateTripUnits(context = context)
        statisticsViewModel.updateOverallDataUnits(context = context)
    }

    fun initializeIsMetricSetting(context: Context) {

        viewModelScope.launch {
            appDataStoreImpl.getIsMetric().collect { isMetric ->
                var isMetricValue = isMetric
                if (isMetricValue == null) isMetricValue = true
                GenerallData.isMetric.value = isMetricValue
            }
        }

    }
}