@file:OptIn(ExperimentalMaterial3Api::class)

package com.speedtracker

import android.annotation.SuppressLint
import android.content.Context
import android.location.GnssStatus
import android.location.GpsStatus
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.*
import com.speedtracker.helper.NavDrawerItem
import com.speedtracker.app.screens.mainscreen.speed.SpeedViewModel
import com.speedtracker.app.screens.mainscreen.statistics.Statistic
import com.speedtracker.app.screens.mainscreen.statistics.StatisticsViewModel
import com.speedtracker.app.screens.walkthrough.WalkthroughViewModel
import com.speedtracker.app.screens.walkthrough.pages.MainScreenView
import com.speedtracker.app.screens.walkthrough.pages.WalkthroughScreen
import com.speedtracker.helper.Constants
import com.speedtracker.helper.GenerallData
import com.speedtracker.model.AppDatabase
import com.speedtracker.model.Location
import com.speedtracker.ui.theme.SpeedTrackerComposeTheme
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable.interval

import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


class MainActivity : DrawerView(),GpsStatus.Listener {

    val speedViewModel by viewModels<SpeedViewModel>()
    val statisticsViewModel by viewModels<StatisticsViewModel>()
    val walkthroughViewModel by viewModels<WalkthroughViewModel>()

    lateinit var scaffoldState:ScaffoldState
    lateinit var scope:CoroutineScope
    lateinit var navController:NavHostController

    var checkAccuracy: Boolean = false
    //Snr > 40
    var highSnrValue: Double = 25.0
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var locationManager: LocationManager

    private var updateTime: Double = 0.0
    private var lastUpdateTime: Long = 0L

    lateinit var observer: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        setContent {
            SpeedTrackerComposeTheme {
                scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
                scope = rememberCoroutineScope()
                navController = rememberNavController()

                // A surface container using the 'background' color from the theme
                Scaffold(
                    scaffoldState = scaffoldState,
                    drawerGesturesEnabled = false,
                    // scrimColor = Color.Red,  // Color for the fade background when you open/close the drawer
                    drawerContent = {
                            Drawer(scope = scope, scaffoldState = scaffoldState, navController = navController)
                    },
                    drawerShape = customShape()
                ) {
                    Navigation(navController = navController,scope,scaffoldState)
                }

            }
        }
    }

    fun customShape() =  object : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline {
            return Outline.Rounded(RoundRect(0f,0f,size.width/5*4,size.height, topRightCornerRadius = CornerRadius(x = 20f, y = 20f), bottomRightCornerRadius = CornerRadius(x = 20f, y = 20f)) )
        }
    }

    @Composable
    fun Navigation(navController: NavHostController, scope: CoroutineScope, scaffoldState: ScaffoldState) {

        scope.launch {
            var carInfos = AppDatabase.getDatabase(this@MainActivity).carInfoDao().getAllCarInfos()
            if (carInfos != null && carInfos.size > 0) {
                navController.navigate("speed-meter")
            } else {
                navController.navigate("walkthrough")
            }
            this@MainActivity.statisticsViewModel.initializeStatisticsData(this@MainActivity)
        }

        NavHost(navController, startDestination = "base") {
            composable("speed-meter") {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreenView(scope = scope,
                        scaffoldState = scaffoldState,
                        speedViewModel = speedViewModel,
                        statisticsViewModel = statisticsViewModel)
                }
            }
            composable("walkthrough") {
                WalkthroughScreen(context = this@MainActivity,
                    walkthroughViewModel = walkthroughViewModel,
                    navigationController = navController)
            }
            composable(NavDrawerItem.Settings.route) {
                Column(modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()) {
                    Text(text = "This is Settings Screen")
                }
            }
            composable("base"){}
        }
    }

    override fun onBackPressed() {
        if (scope != null && scaffoldState != null) {
            if (scaffoldState.drawerState.isOpen) {
                scope.launch {
                    scaffoldState.drawerState.close()
                }
            } else {
                if (navController.currentDestination != null) {
                    if (navController.currentDestination!!.route != "speed-meter") {
                        super.onBackPressed()
                    }
                } else {
                    super.onBackPressed()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onGpsStatusChanged(event: Int) {
        if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            var status = locationManager.getGpsStatus(null)
            if (status != null) {
                val satellites = status.satellites
                var usedSatellites =
                    satellites.filter { satellite -> satellite.usedInFix() == true }
                Log.i("         Used satelites", "\t${usedSatellites.size}")
                val maxSnr = usedSatellites.maxOf { satellite -> satellite.snr }
                speedViewModel.searchingForGPSLocation.value = maxSnr > highSnrValue
                Log.i("          Max snr value", "\t${maxSnr}")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startUpdatingLocation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            locationManager.addGpsStatusListener(this)
        } else {
            locationManager.registerGnssStatusCallback(object : GnssStatus.Callback() {
                override fun onSatelliteStatusChanged(status: GnssStatus) {
                    if (status != null) {
                        val satellitesCount = status.satelliteCount
                        if (satellitesCount > 0) {
                            var status = locationManager.getGpsStatus(null)
                            if (status != null) {
                                val satellites = status.satellites
                                if (satellites != null) {
                                    var usedSatellites =
                                        satellites.filter { satellite -> satellite.usedInFix() == true }
                                    Log.i("         Used satelites", "\t${usedSatellites.size}")
                                    if (usedSatellites != null && usedSatellites.size != 0) {
                                        var maxSnr =
                                            usedSatellites.maxOf { satellite -> satellite.snr }

                                        speedViewModel.searchingForGPSLocation.value =  maxSnr > highSnrValue
                                        Log.d("GPS signal ready", speedViewModel.searchingForGPSLocation.value.toString())
                                        Log.d("GPS max snr value", "\t${maxSnr}")
                                    }
                                }
                            } else {
                                speedViewModel.searchingForGPSLocation.value = false
                            }
                        }
                    }
            } }, null)
        }

        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            var locationRequest = LocationRequest()
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            locationRequest.setInterval(0)
            var locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    Log.i(
                        "-",
                        "---------------------------------------------------------------------"
                    )
                    updateTime = (System.currentTimeMillis() - lastUpdateTime).toDouble() / 1000
                    lastUpdateTime = System.currentTimeMillis()
                    Log.i("   Time between updates", "\t${updateTime} seconds")
                    Log.i(
                        "     Latitude-Longitude",
                        "\t${locationResult.lastLocation.latitude}," +
                                " ${locationResult.lastLocation.longitude}"
                    )
                    Log.i(
                        "                  Speed",
                        "\t${(locationResult.lastLocation.speed).toInt()} m/s"
                    )
                    if (GenerallData.isMetric.value!!) {
                        Log.i(
                            "                  Speed",
                            "\t${(locationResult.lastLocation.speed * Constants.msToKmh).toInt()} km/h"
                        )
                    } else {
                        Log.i(
                            "                  Speed",
                            "\t${(locationResult.lastLocation.speed * Constants.msToMph).toInt()} mil"
                        )
                    }

//                    Toast.makeText(applicationContext,"Accuracy is ${locationResult.lastLocation.accuracy}",Toast.LENGTH_SHORT).show()
                    Log.i("Accuracy", "${locationResult.lastLocation.accuracy}")

                    if (speedViewModel.searchingForGPSLocation.value!! && locationResult.lastLocation.accuracy < 20) {

                        var currentSpeed: Int
                        var minValuableSpeed = 0
                        if (GenerallData.isMetric.value!!) {
                            minValuableSpeed = 5
                            currentSpeed =
                                (locationResult.lastLocation.speed * Constants.msToKmh).toInt()
                        } else {
                            minValuableSpeed = 3
                            currentSpeed =
                                (locationResult.lastLocation.speed * Constants.msToMph).toInt()
                        }
                        Log.i("Current speed", currentSpeed.toString())

                        if (currentSpeed > minValuableSpeed) {
                            speedViewModel.speed.value = (locationResult.lastLocation.speed * Constants.msToKmh).roundToInt()
                            speedViewModel.actualLatitude = locationResult.lastLocation.latitude
                            speedViewModel.actualLongitude = locationResult.lastLocation.longitude
                            speedViewModel.altitude.value = locationResult.lastLocation.altitude
                            speedViewModel.actualTime = locationResult.lastLocation.time
                        } else {
                            speedViewModel.speed.value = 0
                            currentSpeed = 0
                            if (speedViewModel.actualLatitude == 0.0 && speedViewModel.actualLongitude == 0.0 && speedViewModel.actualAltitude == 0.0) {
                                speedViewModel.actualLatitude = locationResult.lastLocation.latitude
                                speedViewModel.actualLongitude = locationResult.lastLocation.longitude
                                speedViewModel.altitude.value = locationResult.lastLocation.altitude
                                speedViewModel.actualTime = locationResult.lastLocation.time
                            }
                        }
                    }
                }
            }
            fusedLocationClient!!.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun startStatsHandler() {
        observer = interval(2000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val current = System.currentTimeMillis()
                var hh = "${(current / 3600000) % 24}"
                var mm = "${(current / 60000) % 60}"
                var ss = "${(current / 1000) % 60}"

                Log.i("Observed", "true")
                if (hh.length == 1) {
                    hh = "0" + hh
                }
                if (mm.length == 1) {
                    mm = "0" + mm
                }
                if (ss.length == 1) {
                    ss = "0" + ss
                }

                var distanceToSave = 0f
                if (speedViewModel.searchingForGPSLocation.value!! && speedViewModel.speed.value != 0) {
                    if (speedViewModel.actualLatitude != 0.0 && speedViewModel.actualLongitude != 0.0) {
                        Log.i("         Rx java update", "\t" + hh + ":" + mm + ":" + ss)
                        if (speedViewModel.lastOverallLatitude != 0.0 && speedViewModel.lastOverallLongitude != 0.0) {
                            distanceToSave = (Math.round(countCurrentDistance() * 10.0) / 10.0).toFloat()
                            storeManager.saveOverallTripData(
                                speed,
                                this@MainActivity,
                                distanceToSave
                            )
                        } else {
                            speedViewModel.lastOverallLatitude = speedViewModel.actualLatitude
                            speedViewModel.lastOverallLongitude = speedViewModel.actualLongitude
                        }


                        if (SingletonData.existsCurrentTrip) {
                            val location = Location(
                                tripIdentifier = statisticsViewModel.trip!!.tripId,
                            latitude = speedViewModel.actualLatitude,
                            longitude = speedViewModel.actualLongitude,
                            altitude = speedViewModel.actualAltitude,
                            time = speedViewModel.actualTime,
                            locationId = )
                            Log.i("Current trip", "updated")
                            storeManager.saveCurrentTripData(
                                speed,
                                location,
                                SingletonData.currentTripId,
                                distanceToSave
                            )
                            if (!SingletonData.appInBackground && !SingletonData.headUpShow) {
                                try {
                                    (adapter.getPage(1) as BottomStatsFragment).updateObjectList(
                                        1
                                    )
                                } catch (e: Exception) {
                                    Log.e("Overall stats error", e.localizedMessage)
                                }
                            }
                        }
                    }
                }
            }
    }
}
