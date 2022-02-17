@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package com.speedtracker.app.screens.about

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Space
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.speedtracker.R
import com.speedtracker.app.screens.trips.TripViewModel
import com.speedtracker.app.screens.trips.triplist.*
import com.speedtracker.app.screens.walkthrough.WalkthroughViewModel
import com.speedtracker.helper.Constants
import com.speedtracker.helper.Formatter
import com.speedtracker.helper.GenerallData
import com.speedtracker.model.AppDatabase
import com.speedtracker.model.CarInfo
import com.speedtracker.ui.theme.MainGradientMiddleColor
import com.speedtracker.ui.theme.MainGradientStartColor
import com.speedtracker.ui.theme.Nunito
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import kotlin.coroutines.coroutineContext


var dataLoaded:MutableLiveData<Boolean> = MutableLiveData(false)
var showNoTripData:MutableLiveData<Boolean> = MutableLiveData(false)

@Composable
fun AboutScreen(scope: CoroutineScope, context: Context, paddingValues: PaddingValues, carInfo: MutableLiveData<CarInfo?>, tripViewModel:TripViewModel, navController: NavController, walkthroughViewModel: WalkthroughViewModel) {

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.about_screen_title), color = Color.White, fontFamily = Nunito) },
                backgroundColor = MainGradientStartColor
            )
        },
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MainGradientStartColor)) {
            Column(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {

                            if (carInfo.observeAsState().value != null) {
                                Log.d("Car photo path", carInfo.observeAsState().value!!.carPhoto!!)

                                imageUri = Uri.parse(carInfo.observeAsState().value!!.carPhoto!!)

                                Image(
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    painter = rememberImagePainter(
                                        data  = imageUri  // or ht
                                    ),
                                    contentDescription = ""
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            listOf(
                                                MainGradientStartColor.copy(alpha = 0.4f),
                                                MainGradientStartColor
                                            )
                                        )
                                    )
                            )

                        val launcher = rememberLauncherForActivityResult(contract =
                        ActivityResultContracts.OpenDocument()) { uri: Uri? ->
                            carInfo.value!!.carPhoto = uri!!.toString()
//                            imageUri = uri
                            val contentResolver = context.contentResolver

                            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            // Check for the freshest data.
                            contentResolver.takePersistableUriPermission(uri, takeFlags)
                            scope.launch {
                                Log.d("New Car photo path", carInfo.value!!.carPhoto!!)
                                walkthroughViewModel.updateCarPhoto(context = context, photoPath = uri.toString(), carInfo = carInfo)
                            }

                        }

                        IconButton(onClick = {
                            launcher.launch(arrayOf("image/*"))
                        }, modifier = Modifier.align(Alignment.TopStart)) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = "Change photo icon", tint = Color.White)
                        }

                        IconButton(onClick = {
                            navController.navigate("edit-car-info")
                        }, modifier = Modifier.align(Alignment.TopEnd)) {
                            Icon(Icons.Default.Edit, contentDescription = "Change car info", tint = Color.White)
                        }
                    }
                }

                LaunchedEffect("") {
                    dataLoaded.value = false
                    tripViewModel.loadTripsByCarInfo(context = context, carInfoId = carInfo.value!!.carIdentifier)
                    dataLoaded.value = true
                }

                if (!dataLoaded.observeAsState().value!!) {
                    Loading()
                } else {
                    var topSpeed:MutableLiveData<String> = MutableLiveData("0")
                    var distance:MutableLiveData<String> = MutableLiveData("0")
                    var timeSpent:MutableLiveData<String> = MutableLiveData("0:00:00")
                    var countOfTrips:MutableLiveData<String> = MutableLiveData("0")

                    var distanceUnits: MutableLiveData<String> = MutableLiveData(if(GenerallData.isMetric.value!!) context.getString(
                        R.string.measute_units_metric) else context.getString(R.string.measute_units_imperial))
                    var speedUnits: MutableLiveData<String> = MutableLiveData(if(GenerallData.isMetric.value!!) context.getString(R.string.speed_units_metric) else context.getString(R.string.speed_units_imperial))

                    if (tripViewModel.tripListByCarInfo.observeAsState().value!!.size == 0) {
                        showNoTripData.value = true
                    } else {
                        showNoTripData.value = false
                    }
                    if (!showNoTripData.observeAsState().value!!) {
                        var topSpeedValue = tripViewModel.tripListByCarInfo.value!!.maxOf { it.tripInfo.maxSpeed }
                        topSpeed.value = "${if(GenerallData.isMetric.value!!) (topSpeedValue * Constants.msToKmh).toInt() else (topSpeedValue * Constants.msToMph).toInt()}"
                        countOfTrips.value = tripViewModel.tripListByCarInfo.value!!.size.toString()
                        var different = 0L
                        var endDate: Date
                        tripViewModel.tripListByCarInfo.value!!.forEach {
                            if (it.tripInfo.tripEndDate == null) {
                                endDate = Calendar.getInstance().time
                            } else {
                                endDate = Date(it.tripInfo.tripEndDate!!)
                            }
                            different += Formatter.calculateTimeBetweenDates(startDate = Date(it.tripInfo.tripStartDate!!), endDate)
                        }
                        timeSpent.value = Formatter.formatTimeFromLong(different = different)

                        var distanceDouble = 0.0
                        tripViewModel.tripListByCarInfo.value!!.forEach {
                            distanceDouble += Formatter.calculateTripDistanceFromLocationList(locations = it.locations)
                        }

                        distance.value = if (GenerallData.isMetric.observeAsState().value!!) "${(Math.round(distanceDouble / Constants.mToKm) * 10.0) / 10.0 }" else "${(Math.round(distanceDouble / Constants.mToMil) * 10.0) / 10.0}"
                    }

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.2f)) {
                        CarInfoView(modifier = Modifier.padding(20.dp), carBrand = MutableLiveData(carInfo.observeAsState().value!!.carBrand), carModel = MutableLiveData(carInfo.observeAsState().value!!.carModel), carYear = MutableLiveData(carInfo.observeAsState().value!!.carManufacturedYear))
                        Text(text = "Trips statistics: ", color = Color.Gray, fontSize = 12.sp, fontFamily = Nunito, modifier = Modifier.padding(start = 20.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .padding(start = 20.dp, end = 20.dp)
                            .background(Color.Gray.copy(alpha = 0.3f)))
                        Column() {
                            Row(modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()) {
                                StatsItem(modifier = Modifier.weight(1f), title = MutableLiveData(stringResource(R.string.about_top_speed_title)), value = topSpeed, units = speedUnits)
                                StatsItem(modifier = Modifier.weight(1f), title = MutableLiveData(stringResource(R.string.about_distance_title)), value = distance, units = distanceUnits)
//                            StatsItem(modifier = Modifier, title = MutableLiveData("Time spent"), value = MutableLiveData("23:15:54"), units = MutableLiveData("hour"))

                            }
                            Row(modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()) {
                                StatsItem(modifier = Modifier.weight(1f), title = MutableLiveData(stringResource(R.string.about_time_spent_title)), value = timeSpent, units = MutableLiveData("hour"))
                                StatsItem(modifier = Modifier.weight(1f), title = MutableLiveData(stringResource(R.string.about_count_of_trips_title)), value = countOfTrips, units = MutableLiveData(""))

                            }

                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewCarInfo() {
    CarInfoView(modifier = Modifier.padding(20.dp), carBrand = MutableLiveData("Skoda"), carModel = MutableLiveData("Octavia Combi"), carYear = MutableLiveData("2015"))
}
@Composable
fun CarInfoView(modifier: Modifier,carBrand:MutableLiveData<String>,carModel:MutableLiveData<String>,carYear:MutableLiveData<String>) {
    Row(modifier = modifier) {
        Column() {
            Text(text = carBrand.observeAsState().value!!, fontSize = 12.sp, fontWeight = FontWeight.Normal, fontFamily = Nunito, color = Color.Gray)
            Text(text = carModel.observeAsState().value!!, fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = Nunito, color = Color.White)
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(modifier = Modifier.align(Alignment.Bottom),text = carYear.observeAsState().value!!, fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = Nunito, color = Color.White)
    }

}

@Preview
@Composable
fun PreviewStatsItem() {
    StatsItem(modifier = Modifier.width(150.dp), title = MutableLiveData("Top Speed"), value = MutableLiveData("150"), units = MutableLiveData("km/h"))
}

@Composable
fun StatsItem(modifier: Modifier,title:MutableLiveData<String>, value:MutableLiveData<String>, units:MutableLiveData<String>) {
    Surface(modifier = modifier, color = Color.Transparent) {
        Column(modifier = Modifier.padding(16.dp),horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title.observeAsState().value!!, fontSize = 12.sp, fontWeight = FontWeight.Normal, fontFamily = Nunito, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value.observeAsState().value!!, fontSize = 30.sp, fontWeight = FontWeight.Bold, fontFamily = Nunito, color = Color.White)
            Text(text = units.observeAsState().value!!, fontSize = 8.sp, fontWeight = FontWeight.Normal, fontFamily = Nunito, color = Color.Gray)
        }
    }
}