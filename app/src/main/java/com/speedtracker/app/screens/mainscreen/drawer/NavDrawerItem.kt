package com.speedtracker.app.screens.mainscreen.drawer

import com.speedtracker.R

sealed class NavDrawerItem(var route: String, var icon: Int, var title: String) {

    object SpeedMeter: NavDrawerItem("speed-meter", R.drawable.trip_icon,"Speed Meter")
    object HeadUpDisplay: NavDrawerItem("head-up", R.drawable.trip_icon,"Head Up Display")
    object Settings : NavDrawerItem("settings", R.drawable.settings_icon, "Settings")
    object TripList : NavDrawerItem("trip-list", R.drawable.trip_list_icon, "Trip List")
    object About : NavDrawerItem("about", R.drawable.ic_baseline_info_24, "About")
}
