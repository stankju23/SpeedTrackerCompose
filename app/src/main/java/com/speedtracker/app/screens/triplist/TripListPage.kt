@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class
)

package com.speedtracker.app.screens.triplist

import android.app.Activity
import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.speedtracker.R
import com.speedtracker.app.screens.mainscreen.StatisticsItem
import com.speedtracker.app.screens.tripscreen.TripPage
import com.speedtracker.helper.Formatter
import com.speedtracker.model.AppDatabase
import com.speedtracker.model.Location
import com.speedtracker.model.TripData
import com.speedtracker.model.TripInfo
import com.speedtracker.ui.theme.MainGradientStartColor
import com.speedtracker.ui.theme.Nunito
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

var dataLoaded:MutableLiveData<Boolean> = MutableLiveData(false)
var showNoTripData:MutableLiveData<Boolean> = MutableLiveData(false)
var trips:MutableLiveData<ArrayList<TripData>> = MutableLiveData(arrayListOf())
var choosedTrip:TripData? = null

lateinit var navController:NavHostController

@Composable
fun TripNavigation(context: Context,scope: CoroutineScope) {
    navController = rememberNavController()
    NavHost(navController = navController, startDestination = "trip-list") {
        composable("trip-list") { TripListPage(context = context, scope = scope)}
        composable("trip-detail") { TripPage(choosedTrip!!, context = context)}
    }
}


@Composable
fun TripListPage(context: Context,scope: CoroutineScope) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Trip List", color = Color.White, fontFamily = Nunito) },
               backgroundColor = MainGradientStartColor,
                navigationIcon = {
                    IconButton(onClick = {(context as Activity).onBackPressed()}) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "BackArrow", tint = Color.White)
                        dataLoaded.value = false
                        showNoTripData.value = false
                    }
                },
            )
        }
    ) {

        if (!dataLoaded.observeAsState().value!!) {
//            SideEffect {
//
//            }

            LaunchedEffect("") {
                trips.value = ArrayList(AppDatabase.getDatabase(context = context).tripDao().getAllTripData().reversed())
                dataLoaded.value = true
            }

            Loading()
        } else {
            if (trips.observeAsState().value!!.size == 0) {
                showNoTripData.value = true
            } else {
                showNoTripData.value = false
            }
            if (showNoTripData.observeAsState().value!!) {
                NoTrip()
            } else {
                TripList(tripList = trips.value!!,context = context)
            }
        }

    }
}

@Composable
fun Loading() {
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        CircularProgressIndicator(
            modifier = Modifier.size(30.dp),
            color = Color.Gray,
            strokeWidth = 2.5.dp
        )
    }
}

@Preview
@Composable
fun NoTripPrew() {
    NoTrip()
}

@Composable
fun NoTrip() {
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Image(modifier = Modifier.size(150.dp),
            painter = painterResource(id = R.drawable.no_trips_sad_smiley) ,
            contentDescription = "No trips image")
        Text(text = stringResource(id = R.string.no_trip_info_text),
            textAlign = TextAlign.Center,
            color = Color.Gray,
            fontFamily = Nunito,
            fontSize = 19.sp,
            modifier = Modifier.padding(24.dp))
    }
}

@Composable
fun TripList(tripList:List<TripData>,context: Context) {
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(top = 4.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(tripList.size) { index ->

            val dismissState = rememberDismissState()

            if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                trips.value!!.removeAt(index)
            }

            SwipeToDismiss(
                state = dismissState,
                modifier = Modifier
                    .padding(vertical = Dp(1f)),
                directions = setOf(
                    DismissDirection.EndToStart
                ),
                dismissThresholds = { direction ->
                    FractionalThreshold(if (direction == DismissDirection.EndToStart) 0.1f else 0.05f)
                },
                background = {
                    val color by animateColorAsState(
                        when (dismissState.targetValue) {
                            DismissValue.Default -> Color.White
                            else -> Color.Red
                        }
                    )
                    val alignment = Alignment.CenterEnd

                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = Dp(20f)),
                        contentAlignment = alignment
                    ) {
                    }
                },
                dismissContent = {
                    if (trips.value!!.size != 0) {
                        TripListItem(tripData = tripList.get(index), context = context)
                    } else {
                        showNoTripData.value = true
                    }
                }
            )
        }

    }
}

@Preview
@Composable
fun TripListItemPreview() {
    TripListItem(TripData(
        TripInfo(tripId = 1L,
            tripName = "Ochodnica",
            sumOfTripSpeed = 0,
            countOfUpdates = 0,
            maxSpeed = 100,
            distance = 100.0,
            tripStartDate = Calendar.getInstance().time.time,
            tripEndDate = Calendar.getInstance().time.time),
        locations = arrayListOf(Location(0,0L,18.13,49.2))
    ), context = LocalContext.current)
}

@Composable
fun TripListItem(tripData: TripData,context:Context) {

//    var filterLocations = tripData.locations
    var filterLocations = tripData.locations.filter { location -> location.latitude != 0.0 && location.longitude != 0.0 }
    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(70.dp)
        .background(Color.White),
        elevation = 4.dp,
    shape = RoundedCornerShape(4.dp)) {
        
        if (filterLocations.size == 0) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material.Icon(
                    painter = painterResource(id = R.drawable.car_icon),
                    contentDescription = "Tripicon",
                    modifier = Modifier
                        .padding(start = 15.dp, end = 15.dp)
                        .size(35.dp)
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Corrupted locations swipe to remove",
                        color = Color.DarkGray,
                        fontSize = 16.sp,
                        fontFamily = Nunito
                    )
                }
            }
        } else {
            Row(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clickable {
                    navController.navigate("trip-detail")
                    choosedTrip = tripData
                },
                verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material.Icon(
                    painter = painterResource(id =R.drawable.car_icon),
                    contentDescription = "Tripicon",
                    modifier = Modifier
                        .padding(start = 15.dp, end = 15.dp)
                        .size(35.dp)
                )
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = tripData.tripInfo.tripName!!.replaceFirstChar { it.uppercase()},
                            color = Color.DarkGray,
                            fontSize = 16.sp,
                            fontFamily = Nunito,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f) )
                        Text(text = Formatter.formatDate(tripData.tripInfo.tripStartDate!!),
                            modifier = Modifier
                                .padding(start = 15.dp, end = 15.dp),
                            color = Color.Gray,
                            fontSize = 10.sp)
                    }

                    Row(modifier  = Modifier
                        .fillMaxWidth()
                        .padding(end = 15.dp, top = 4.dp)) {
                        Text(text = Formatter.getCityFromLocation(filterLocations.first(), context = context)!!,
                            fontFamily = Nunito,
                            fontSize = 10.sp,
                            color = Color.Gray)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Filled.ArrowForward, contentDescription = "ToIcon", modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = Formatter.getCityFromLocation(filterLocations.last(), context = context)!!,
                            fontFamily = Nunito,
                            fontSize = 10.sp,
                            color = Color.Gray)
                    }
                }
            }
        }
    }
}