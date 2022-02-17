package com.speedtracker.app.screens.settings

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
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

    fun showWebPage(url:String,context:Context) {
        val uri: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }


    fun resetApp(context: Context) {
        (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .clearApplicationUserData()
        (context as Activity).recreate()
    }

}