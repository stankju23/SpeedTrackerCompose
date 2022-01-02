package com.speedtracker.helper

import com.speedtracker.R

sealed class NavDrawerItem(var route: String, var icon: Int, var title: String) {

    object SpeedMeter:NavDrawerItem("speed-meter", R.drawable.trip_icon,"Speed Meter")
    object HeadUpDisplay:NavDrawerItem("head-up", R.drawable.trip_icon,"Head Up Display")
    object TripList:NavDrawerItem("trip-list", R.drawable.trip_icon,"Trip List")
    object Settings : NavDrawerItem("settings", R.drawable.trip_icon, "Settings")
}
