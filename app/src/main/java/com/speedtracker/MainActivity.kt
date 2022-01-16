@file:OptIn(ExperimentalMaterial3Api::class)

package com.speedtracker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.GnssStatus
import android.location.GpsStatus
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.speedtracker.app.screens.mainscreen.drawer.DrawerView
import com.speedtracker.app.screens.mainscreen.drawer.NavDrawerItem
import com.speedtracker.app.screens.mainscreen.speed.SpeedViewModel
import com.speedtracker.app.screens.mainscreen.statistics.StatisticsViewModel
import com.speedtracker.app.screens.triplist.TripListPage
import com.speedtracker.app.screens.walkthrough.WalkthroughViewModel
import com.speedtracker.app.screens.walkthrough.pages.MainScreenView
import com.speedtracker.app.screens.walkthrough.pages.WalkthroughScreen
import com.speedtracker.helper.Constants
import com.speedtracker.helper.GenerallData
import com.speedtracker.model.AppDatabase
import com.speedtracker.model.CarInfo
import com.speedtracker.model.Location
import com.speedtracker.ui.theme.MainGradientBG
import com.speedtracker.ui.theme.SpeedTrackerComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable.interval

import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@HiltAndroidApp
class CoreApplication: Application()

@AndroidEntryPoint
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

    var showTripDialog:MutableLiveData<Boolean> = MutableLiveData(false)
    var tripName:MutableLiveData<String> = MutableLiveData("")
    var carInfo:MutableLiveData<CarInfo?> = MutableLiveData()

    var startDestination:String = "base"

    var canUpdateSpeed:Boolean = false


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
                            Drawer(scope = scope, scaffoldState = scaffoldState, navController = navController,carInfo = carInfo.observeAsState().value, context = this@MainActivity)
                    },
                    drawerShape = customShape()
                ) {
                    checkGPSPermission()
                    Navigation(navController = navController,scope,scaffoldState)
                }

            }
        }

        GlobalScope.launch((Dispatchers.IO)) {
//            statisticsViewModel.getAllTrips(context = this@MainActivity)
            var carInfos = AppDatabase.getDatabase(this@MainActivity).carInfoDao().getAllCarInfos()
//            AppDatabase.getDatabase(this@MainActivity).carInfoDao().deleteCarInfos()
            if (carInfos != null && carInfos.size > 0) {
                startDestination = "speed-meter"
                runOnUiThread {
                    this@MainActivity.statisticsViewModel.initializeStatisticsData(this@MainActivity)
                    canUpdateSpeed = true
                    carInfo.value = carInfos.last()
                }
            } else {
                startDestination = "walkthrough"
                canUpdateSpeed = false
//                navController.navigate("walkthrough")
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
    fun checkGPSPermission() {
//        val multiplePermissionsState = rememberMultiplePermissionsState(
//            listOf(
//                android.Manifest.permission.ACCESS_FINE_LOCATION,
//                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
//                android.Manifest.permission.ACCESS_COARSE_LOCATION
//            )
//        )
//
//        multiplePermissionsState.launchMultiplePermissionRequest()
//
//
//        val context: Context = LocalContext.current
//
//        val settingResultRequest = rememberLauncherForActivityResult(
//            contract = ActivityResultContracts.StartIntentSenderForResult()
//        ) { activityResult ->
//            if (activityResult.resultCode == RESULT_OK)
//                Log.d("appDebug", "Accepted")
//            else {
//                Log.d("appDebug", "Denied")
//            }
//        }
//            checkLocationSetting(
//                context = context,
//                onDisabled = { intentSenderRequest ->
//                    settingResultRequest.launch(intentSenderRequest)
//                },
//                onEnabled = {
//                /* This will call when setting is already enabled */
//                }
//            )

        val permissionGranted = ContextCompat.checkSelfPermission(this,  Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {
            Log.d(TAG, "Permission already granted, exiting..")
            startUpdatingLocation()
            SideEffect {
                startStatsHandler()
            }

            return
        }


        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d(TAG, "Permission provided by user")
                // Permission Accepted
                startUpdatingLocation()
                startStatsHandler()
            } else {
                Log.d(TAG, "Permission denied by user")
                // Permission Denied

            }
        }
        SideEffect {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }


    }


    // call this function on button click
    fun checkLocationSetting(
        context: Context,
        onDisabled: (IntentSenderRequest) -> Unit,
        onEnabled: () -> Unit
    ) {

        val locationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest
            .Builder()
            .addLocationRequest(locationRequest)

        val gpsSettingTask: Task<LocationSettingsResponse> =
            client.checkLocationSettings(builder.build())

        gpsSettingTask.addOnSuccessListener { onEnabled() }
        gpsSettingTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest = IntentSenderRequest
                        .Builder(exception.resolution)
                        .build()
                    onDisabled(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // ignore here
                }
            }
        }

    }

    @Composable
    fun Navigation(navController: NavHostController, scope: CoroutineScope, scaffoldState: ScaffoldState) {

        NavHost(navController, startDestination = startDestination) {
            composable("speed-meter") {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreenView(scope = scope,
                        scaffoldState = scaffoldState,
                        speedViewModel = speedViewModel,
                        statisticsViewModel = statisticsViewModel,
                        context = this@MainActivity,
                        showTripDialog = showTripDialog,
                        tripName = tripName)

                }
            }
            composable("walkthrough") {
                WalkthroughScreen(context = this@MainActivity,
                    walkthroughViewModel = walkthroughViewModel,
                    navigationController = navController,
                    statisticsViewModel = statisticsViewModel,
                    carInfo = carInfo)
            }
            composable(NavDrawerItem.Settings.route) {
                Column(modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()) {
                    Text(text = "This is Settings Screen")
                }
            }
            composable("base"){
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(brush = MainGradientBG),
                    contentAlignment = Alignment.Center) {
                    Image(modifier = Modifier.size(100.dp),painter = painterResource(id = R.drawable.ic_car_splash), contentDescription = "SplashIcon")
                }
            }
            composable(NavDrawerItem.TripList.route){
                Box(modifier = Modifier
                    .fillMaxSize(),
                    ) {
                    TripListPage(this@MainActivity, scope = scope)
                }
            }
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
                var usedSatellites = satellites.filter { satellite -> satellite.usedInFix() == true }
                Log.i("         Used satelites", "\t${usedSatellites.size}")
                this.speedViewModel.satellitesText.value = "${usedSatellites.size}/${satellites.count()}"
                val maxSnr = usedSatellites.maxOf { satellite -> satellite.snr }
                this@MainActivity.speedViewModel.searchingForGPSLocation.value = maxSnr < highSnrValue
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
                                    this@MainActivity.speedViewModel.satellitesText.value = "${usedSatellites.size}/${satellites.count()}"
                                    if (usedSatellites != null && usedSatellites.size != 0) {
                                        var maxSnr =
                                            usedSatellites.maxOf { satellite -> satellite.snr }

                                        speedViewModel.searchingForGPSLocation.value =  maxSnr < highSnrValue
                                        Log.d("GPS searching for signal", speedViewModel.searchingForGPSLocation.value.toString())
                                        Log.d("GPS max snr value", "\t${maxSnr}")
                                    }
                                }
                            } else {
                                speedViewModel.searchingForGPSLocation.value = true
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

                    if (!speedViewModel.searchingForGPSLocation.value!! && locationResult.lastLocation.accuracy < 20) {

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
//                            speedViewModel.animateSpeed(speedViewModel.speed.value!!, )
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
                if (canUpdateSpeed) {
                    val current = System.currentTimeMillis()
                    var hh = "${(current / 3600000) % 24}"
                    var mm = "${(current / 60000) % 60}"
                    var ss = "${(current / 1000) % 60}"

                    Log.d("Satelites", speedViewModel.satellitesText.value!!)
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

                    var distanceToSave = 0.0
                    if (!speedViewModel.searchingForGPSLocation.value!! && speedViewModel.speed.value != 0) {
                        if (speedViewModel.actualLatitude != 0.0 && speedViewModel.actualLongitude != 0.0) {
                            Log.i("         Rx java update", "\t" + hh + ":" + mm + ":" + ss)
                            if (speedViewModel.lastOverallLatitude != 0.0 && speedViewModel.lastOverallLongitude != 0.0) {
                                distanceToSave = (Math.round(speedViewModel.countCurrentDistance() * 10.0) / 10.0)
                                statisticsViewModel.updateOverallData(speed = speedViewModel.speed.value!!,distanceToSave = distanceToSave, context = this)
                            } else {
                                speedViewModel.lastOverallLatitude = speedViewModel.actualLatitude
                                speedViewModel.lastOverallLongitude = speedViewModel.actualLongitude
                            }

                            if (statisticsViewModel.trip.value != null) {
                                val location = Location(
                                    tripIdentifier = statisticsViewModel.trip.value!!.tripId,
                                    latitude = speedViewModel.actualLatitude,
                                    longitude = speedViewModel.actualLongitude,
                                    altitude = speedViewModel.actualAltitude,
                                    time = speedViewModel.actualTime,
                                    locationId = Calendar.getInstance().time.time.toInt())
                                Log.i("Current trip", "updated")
                                statisticsViewModel.updateTrip(speed = speedViewModel.speed.value!!, distanceToSave = distanceToSave, location = location, context = this)
                            }
                        }
                    }
                }
            }
    }
}
