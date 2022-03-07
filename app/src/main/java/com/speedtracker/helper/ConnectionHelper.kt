package com.speedtracker.helper

import android.content.Context
import android.net.ConnectivityManager


object ConnectionHelper {


    fun getInternetConnection(context: Context):Boolean {
        val nInfo = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        return nInfo != null && nInfo.isAvailable && nInfo.isConnected
    }
}