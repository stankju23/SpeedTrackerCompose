@file:OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalAnimationApi::class
)

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
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.speedtracker.app.screens.about.AboutScreen
import com.speedtracker.app.screens.editcarinfo.EditCarInfoScreen
import com.speedtracker.app.screens.headup.HeadUpScreen
import com.speedtracker.app.screens.mainscreen.drawer.NavDrawerItem
import com.speedtracker.app.screens.mainscreen.speed.SpeedViewModel
import com.speedtracker.app.screens.mainscreen.statistics.StatisticsViewModel
import com.speedtracker.app.screens.settings.SettingsScreen
import com.speedtracker.app.screens.settings.SettingsViewModel
import com.speedtracker.app.screens.trips.TripViewModel
import com.speedtracker.app.screens.trips.triplist.TripListPage
import com.speedtracker.app.screens.trips.tripmap.TripMapPage
import com.speedtracker.app.screens.walkthrough.WalkthroughViewModel
import com.speedtracker.app.screens.mainscreen.MainScreenView
import com.speedtracker.app.screens.walkthrough.pages.WalkthroughScreen
import com.speedtracker.helper.AssetsHelper
import com.speedtracker.helper.Constants
import com.speedtracker.helper.GenerallData
import com.speedtracker.model.AppDatabase
import com.speedtracker.model.CarInfo
import com.speedtracker.model.Location
import com.speedtracker.ui.theme.*
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

@HiltAndroidApp
class CoreApplication : Application()


@AndroidEntryPoint
class MainActivity : ComponentActivity(), GpsStatus.Listener {

    val speedViewModel by viewModels<SpeedViewModel>()

    val speedeViewModel by viewModels<SpeedViewModel>()

    val statisticsViewModel by viewModels<StatisticsViewModel>()
    val walkthroughViewModel by viewModels<WalkthroughViewModel>()
    val tripViewModel by viewModels<TripViewModel>()
    val settingsViewModel by viewModels<SettingsViewModel>()

    var showSettingsDialog = MutableLiveData(false)

    //    lateinit var scaffoldState:ScaffoldState
    lateinit var scope: CoroutineScope
    lateinit var navController: NavHostController

    var checkAccuracy: Boolean = false

    val maxAccuracy = 15

    //Snr > 40
    var highSnrValue: Double = 30.0
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var locationManager: LocationManager

    private var updateTime: Double = 0.0
    private var lastUpdateTime: Long = 0L

    lateinit var observer: Disposable

    var showTripDialog: MutableLiveData<Boolean> = MutableLiveData(false)
    var tripName: MutableLiveData<String> = MutableLiveData("")
    var carInfo: MutableLiveData<CarInfo?> = MutableLiveData()

    var startDestination: String = "splash-screen"

    var canUpdateSpeed: Boolean = false
    lateinit var drawerState: MutableState<DrawerValue>

    var showBottomView:MutableLiveData<Boolean?> = MutableLiveData(null)

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager


            GlobalScope.launch((Dispatchers.IO)) {
//            statisticsViewModel.getAllTrips(context = this@MainActivity)
                var carInfos = AppDatabase.getDatabase(this@MainActivity).carInfoDao().getAllCarInfos()
//            AppDatabase.getDatabase(this@MainActivity).carInfoDao().deleteCarInfos()
                if (carInfos != null && carInfos.size > 0) {
                    startDestination = "speed-meter"
                    runOnUiThread {
                        this@MainActivity.statisticsViewModel.initializeStatisticsData(
                            this@MainActivity,
                            settingsViewModel = settingsViewModel
                        )
                        canUpdateSpeed = true
                        carInfo.value = carInfos.last()
                        setContent {
                            SpeedTrackerComposeTheme {
                                drawerState = remember { mutableStateOf(DrawerValue.Closed) }


//                scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
                                scope = rememberCoroutineScope()
                                navController = rememberAnimatedNavController()

                                // A surface container using the 'background' color from the theme
                                CustomScaffold {
                                    checkGPSPermission()
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                    ) {
                                        Navigation(
                                            navController = navController,
                                            scope,
                                            drawerState,
                                            paddingValues = it
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {

                    runOnUiThread {
                        setContent {
                            SpeedTrackerComposeTheme {
                                drawerState = remember { mutableStateOf(DrawerValue.Closed) }
                                scope = rememberCoroutineScope()
                                navController = rememberAnimatedNavController()

                                // A surface container using the 'background' color from the theme
                                if (showBottomView.observeAsState().value != null) {
                                    startDestination = "speed-meter"
                                    canUpdateSpeed = false
                                    CustomScaffold {
                                        checkGPSPermission()
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                        ) {
                                            Navigation(
                                                navController = navController,
                                                scope,
                                                drawerState,
                                                paddingValues = it
                                            )
                                        }
                                    }
                                } else {
                                    WalkthroughScreen(
                                        context = this@MainActivity,
                                        walkthroughViewModel = walkthroughViewModel,
                                        navigationController = navController,
                                        statisticsViewModel = statisticsViewModel,
                                        settingsViewModel = settingsViewModel,
                                        carInfo = carInfo
                                    )
                                }

                            }
                        }
                    }
                }
            }
    }

    @Composable
    fun CustomScaffold(content:@Composable ((PaddingValues) -> Unit)) {
        Scaffold(
            bottomBar = {
                BottomAppBar(
                    modifier = Modifier
                        .background(Color.Transparent),
                    cutoutShape = CircleShape,
                    backgroundColor = MainGradientMiddleColor

                ) {
                    BottomNavigation(navController = navController)
                }
            },
            floatingActionButton = {
                FloatingActionButton(modifier = Modifier,onClick = {
                    navController.navigate(route = "speed-meter") {

                        navController.graph.startDestinationRoute?.let { screen_route ->
                            popUpTo(screen_route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                    backgroundColor = MainGradientMiddleColor
                ) {
                    Icon(painter = painterResource(id = R.drawable.tachometer), "", tint = Color.White)
                }
            },

            floatingActionButtonPosition = FabPosition.Center,
            isFloatingActionButtonDocked = true
        ) {
            content(it)
        }
    }

    fun customShape() = object : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline {
            return Outline.Rounded(
                RoundRect(
                    0f,
                    0f,
                    size.width / 5 * 4,
                    size.height,
                    topRightCornerRadius = CornerRadius(x = 20f, y = 20f),
                    bottomRightCornerRadius = CornerRadius(x = 20f, y = 20f)
                )
            )
        }
    }

    @Composable
    fun BottomNavigation(navController: NavController) {
        val items = listOf(
            NavDrawerItem.TripList,
            NavDrawerItem.HeadUpDisplay,
            NavDrawerItem.SpeedMeter,
            NavDrawerItem.Settings,
            NavDrawerItem.About

        )
        BottomNavigation(
            backgroundColor = MainGradientMiddleColor
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            items.forEach { item ->
                BottomNavigationItem(
                    icon = {
                        if (item.icon != null) {
                            Icon(
                                painterResource(id = item.icon!!),
                                contentDescription = item.title,
                                tint = Color.White,
                                modifier = Modifier.size(25.dp)
                            )
                        }

                    },
                    label = {
                        Text(
                            text = item.title,
                            fontSize = 9.sp,
                            color = Color.White
                        )
                    },
                    selectedContentColor = Color.White,
                    alwaysShowLabel = true,
                    selected = currentRoute == item.route,
                    onClick = {
                        if (item.icon != null) {
                            navController.navigate(item.route) {

                                navController.graph.startDestinationRoute?.let { screen_route ->
                                    popUpTo(screen_route) {
                                        saveState = true
                                    }
                                }
                                launchSingleTop = true
                                restoreState = false
                            }
                        }
                    }
                )
            }
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
        val settingResultRequest = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult()
        ) { activityResult ->
            if (activityResult.resultCode == RESULT_OK) {
                Log.d("appDebug", "Accepted")
                startUpdatingLocation()
                startStatsHandler()
            } else {
                Log.d("appDebug", "Denied")
            }
        }
//            checkLocationSetting(
//                context = context,
//                onDisabled = { intentSenderRequest ->
//                    settingResultRequest.launch(intentSenderRequest)
//                },
//                onEnabled = {
//                /* This will call when setting is already enabled */
//                }
//            )

        val permissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {
            checkLocationSetting(context = this,
                onEnabled = {
                    startUpdatingLocation()
                    startStatsHandler()
                },
                onDisabled = {
                    settingResultRequest.launch(it)
                }
            )
            return
        }


        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d(TAG, "Permission provided by user")
                // Permission Accepted
                checkLocationSetting(context = this,
                    onEnabled = {
                        startUpdatingLocation()
                        startStatsHandler()
                    },
                    onDisabled = {
                        settingResultRequest.launch(it)
                    }
                )
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
    fun Navigation(
        navController: NavHostController,
        scope: CoroutineScope,
        scaffoldState: MutableState<DrawerValue>,
        paddingValues: PaddingValues
    ) {

        AnimatedNavHost(navController, startDestination = startDestination) {
            composable("speed-meter",
                enterTransition = { null },
                popEnterTransition = { null },
                exitTransition = { null },
                popExitTransition = { null }) {
//                Surface(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(bottom = 64.dp),
//                    color = MaterialTheme.colorScheme.background
//                ) {
                canUpdateSpeed = true
                MainScreenView(
                    paddingValues = paddingValues,
                    scope = scope,
                    scaffoldState = scaffoldState,
                    speedViewModel = speedViewModel,
                    statisticsViewModel = statisticsViewModel,
                    context = this@MainActivity,
                    showTripDialog = showTripDialog,
                    tripName = tripName,
                    carInfoId = this@MainActivity.carInfo.value!!.carIdentifier
                )

//                }
            }

            composable("walkthrough") {

                WalkthroughScreen(
                    context = this@MainActivity,
                    walkthroughViewModel = walkthroughViewModel,
                    navigationController = navController,
                    statisticsViewModel = statisticsViewModel,
                    carInfo = carInfo,
                    settingsViewModel = this@MainActivity.settingsViewModel
                )

            }

            composable(route = NavDrawerItem.Settings.route) {
                SettingsScreen(
                    paddingValues = paddingValues,
                    context = this@MainActivity,
                    settingsViewModel = this@MainActivity.settingsViewModel,
                    statisticsViewModel = this@MainActivity.statisticsViewModel
                )

            }

            composable("edit-car-info",
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 300 },
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = FastOutSlowInEasing
                        )
                    )
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 300 },
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = FastOutSlowInEasing
                        )
                    )
                }) {
                var carList = AssetsHelper.parseCarsBrands(this@MainActivity)
                var brandStringList = carList.map { car -> car.brand }
                AssetsHelper.sortArrayAlphabetically(brandStringList as ArrayList<String>)
                brandStringList.add(0, stringResource(id = R.string.choose_brand_title))
                walkthroughViewModel.brandList.value = brandStringList
                Log.d(
                    "Manufactured year",
                    MutableLiveData(carInfo.value!!.carManufacturedYear.toInt()).toString()
                )
                walkthroughViewModel.initializeBrandAndModelFromCarInfo(
                    brand = carInfo.value!!.carBrand,
                    model = carInfo.value!!.carModel,
                    carList = carList,
                    manufacturedYear = MutableLiveData(carInfo.value!!.carManufacturedYear.toInt())
                )

                EditCarInfoScreen(
                    carList = carList,
                    walkthroughViewModel = this@MainActivity.walkthroughViewModel,
                    context = this@MainActivity,
                    carInfo = this@MainActivity.carInfo,
                    scope = scope
                )

            }


            composable("splash-screen") {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(brush = MainGradientBG),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.size(100.dp),
                        painter = painterResource(id = R.drawable.ic_car_splash),
                        contentDescription = "SplashIcon"
                    )
                }
            }

            composable(
                NavDrawerItem.TripList.route
            ) {
                TripListPage(
                    paddingValues = paddingValues,
                    context = this@MainActivity,
                    tripViewModel = tripViewModel,
                    navController = navController
                )
            }

            composable("trip-detail",
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 300 },
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = FastOutSlowInEasing
                        )
                    )
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 300 },
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = FastOutSlowInEasing
                        )
                    )
                }
            ) {
                TripMapPage(
                    paddingValues = paddingValues,
                    context = this@MainActivity,
                    tripViewModel = tripViewModel
                )
            }

            composable(NavDrawerItem.HeadUpDisplay.route) {

                Scaffold(modifier = Modifier.fillMaxSize()) {
                    HeadUpScreen(speedViewModel = this@MainActivity.speedViewModel)
                }
            }

            composable(NavDrawerItem.About.route) {
                AboutScreen(paddingValues = paddingValues, carInfo = this@MainActivity.carInfo, context = this@MainActivity, tripViewModel = this@MainActivity.tripViewModel, navController = navController, walkthroughViewModel = this@MainActivity.walkthroughViewModel, scope = scope)
            }
        }
    }


    override fun onBackPressed() {
        if (scope != null && drawerState != null) {
            if (drawerState.value == DrawerValue.Open) {
                scope.launch {
                    drawerState.value = DrawerValue.Closed
//                    scaffoldState.drawerState.close()
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
                this.speedViewModel.satellitesText.value =
                    "${usedSatellites.size}/${satellites.count()}"
                val maxSnr = usedSatellites.maxOf { satellite -> satellite.snr }
                this@MainActivity.speedViewModel.searchingForGPSLocation.value =
                    maxSnr < highSnrValue
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
                            var usedSatellites = 0
                            var maxSnr = 0f
                            for (i in 0..satellitesCount - 1) {
                                if (status.usedInFix(i))
                                    usedSatellites++
                                if (maxSnr < status.getCn0DbHz(i))
                                    maxSnr = status.getCn0DbHz(i)
                            }

                            if (usedSatellites == 0) {
                                speedViewModel.searchingForGPSLocation.value = true
                            }
                            this@MainActivity.speedViewModel.satellitesText.value =
                                "${usedSatellites}/${status.satelliteCount}"
                            speedViewModel.searchingForGPSLocation.value =
                                maxSnr < highSnrValue
                            Log.d(
                                "GPS searching for signal",
                                speedViewModel.searchingForGPSLocation.value.toString()
                            )
                        } else {
                            speedViewModel.searchingForGPSLocation.value = true
                        }

                        // old way before android 7, our min SDK is 24 which is 7.0 so it's not needed
//                        if (satellitesCount > 0) {
//                            var status = locationManager.getGpsStatus(null)
//                            if (status != null) {
//                                val satellites = status.satellites
//                                if (satellites != null) {
//                                    var usedSatellites =
//                                        satellites.filter { satellite -> satellite.usedInFix() == true }
//                                    Log.i("         Used satelites", "\t${usedSatellites.size}")
//                                    this@MainActivity.speedViewModel.satellitesText.value =
//                                        "${usedSatellites.size}/${satellites.count()}"
//                                    if (usedSatellites != null && usedSatellites.size != 0) {
//                                        var maxSnr =
//                                            usedSatellites.maxOf { satellite -> satellite.snr }
//
//                                        speedViewModel.searchingForGPSLocation.value =
//                                            maxSnr < highSnrValue
//                                        Log.d(
//                                            "GPS searching for signal",
//                                            speedViewModel.searchingForGPSLocation.value.toString()
//                                        )
//                                        Log.d("GPS max snr value", "\t${maxSnr}")
//                                    }
//                                }
//                            } else {
//                                speedViewModel.searchingForGPSLocation.value = true
//                            }
//                        }
                    } else {
                        speedViewModel.searchingForGPSLocation.value = true
                    }
                }
            }, null)
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
                        "\t${locationResult.lastLocation?.latitude}," +
                                " ${locationResult.lastLocation?.longitude}"
                    )
                    Log.i(
                        "                  Speed",
                        "\t${(locationResult.lastLocation?.speed)?.toInt()} m/s"
                    )
                    if (GenerallData.isMetric.value!!) {
                        Log.i(
                            "                  Speed",
                            "\t${((locationResult.lastLocation?.speed ?: (0 * Constants.msToKmh))).toInt()} km/h"
                        )
                    } else {
                        Log.i(
                            "                  Speed",
                            "\t${((locationResult.lastLocation?.speed ?: (0 * Constants.msToMph))).toInt()} mil"
                        )
                    }
//                    Toast.makeText(applicationContext,"Accuracy is ${locationResult.lastLocation.accuracy}",Toast.LENGTH_SHORT).show()
                    Log.i("Accuracy", "${locationResult.lastLocation?.accuracy}")

                    if (!speedViewModel.searchingForGPSLocation.value!! && ((locationResult.lastLocation?.accuracy
                            ?: 0) < (maxAccuracy as Nothing))
                    ) {

                        var currentSpeed: Int
                        var minValuableSpeed = 0
                        if (GenerallData.isMetric.value!!) {
                            minValuableSpeed = 5
                            currentSpeed =
                                ((locationResult.lastLocation?.speed ?: (0 * Constants.msToKmh))).toInt()
                        } else {
                            minValuableSpeed = 3
                            currentSpeed =
                                ((locationResult.lastLocation?.speed ?: (0 * Constants.msToMph))).toInt()
                        }
                        Log.i("Current speed", currentSpeed.toString())

                        speedViewModel.speedToSave = locationResult.lastLocation?.speed ?: 0f

                        if (currentSpeed > minValuableSpeed) {
                            speedViewModel.speed.value = currentSpeed
//                            speedViewModel.animateSpeed(speedViewModel.speed.value!!, )
                            speedViewModel.actualLatitude = locationResult.lastLocation?.latitude ?: 0.0
                            speedViewModel.actualLongitude = locationResult.lastLocation?.longitude ?: 0.0
                            speedViewModel.altitude.value = locationResult.lastLocation?.altitude ?: 0.0
                            speedViewModel.actualTime = locationResult.lastLocation?.time ?: 0
                        } else {
                            speedViewModel.speed.value = 0
                            currentSpeed = 0
                            if (speedViewModel.actualLatitude == 0.0 && speedViewModel.actualLongitude == 0.0 && speedViewModel.actualAltitude == 0.0) {
                                speedViewModel.actualLatitude = locationResult.lastLocation?.latitude ?: 0.0
                                speedViewModel.actualLongitude =
                                    locationResult.lastLocation?.longitude ?: 0.0
                                speedViewModel.altitude.value = locationResult.lastLocation?.altitude ?: 0.0
                                speedViewModel.actualTime = locationResult.lastLocation?.time ?: 0
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
        observer = interval(1000, TimeUnit.MILLISECONDS)
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
                                distanceToSave =
                                    (Math.round(speedViewModel.countCurrentDistance() * 10.0) / 10.0)
                                statisticsViewModel.updateOverallData(
                                    speed = speedViewModel.speedToSave,
                                    distanceToSave = distanceToSave,
                                    context = this
                                )
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
                                    locationId = Calendar.getInstance().time.time.toInt()
                                )
                                Log.i("Current trip", "updated")
                                statisticsViewModel.updateTrip(
                                    speed = speedViewModel.speedToSave,
                                    distanceToSave = distanceToSave,
                                    location = location,
                                    context = this
                                )
                            }
                        }
                    } else {
                        if (statisticsViewModel.trip.value != null) {
                           statisticsViewModel.updateTripDuration(this)
                        }
                    }
                }
            }
    }
}

/**
 * Possible values of [DrawerState].
 */
enum class DrawerValue {
    /**
     * The state of the drawer when it is closed.
     */
    Closed,

    /**
     * The state of the drawer when it is open.
     */
    Open
}
