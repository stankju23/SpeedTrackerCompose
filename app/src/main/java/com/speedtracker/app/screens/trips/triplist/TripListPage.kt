@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class
)

package com.speedtracker.app.screens.trips.triplist

import android.app.Activity
import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.speedtracker.R
import com.speedtracker.app.screens.trips.tripmap.TripMapPage
import com.speedtracker.app.screens.trips.TripViewModel
import com.speedtracker.ui.theme.MainGradientStartColor
import com.speedtracker.ui.theme.Nunito
import kotlinx.coroutines.CoroutineScope

var dataLoaded:MutableLiveData<Boolean> = MutableLiveData(false)
var showNoTripData:MutableLiveData<Boolean> = MutableLiveData(false)

lateinit var navController:NavHostController

@Composable
fun TripNavigation(context: Context,scope: CoroutineScope,tripViewModel: TripViewModel) {
    navController = rememberNavController()
    NavHost(navController = navController, startDestination = "trip-list") {
        composable("trip-list") { TripListPage(context = context, tripViewModel = tripViewModel)}
        composable("trip-detail") { TripMapPage(context = context, tripViewModel = tripViewModel) }
    }
}


@Composable
fun TripListPage(context: Context,tripViewModel: TripViewModel) {
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
                tripViewModel.loadTrips(context = context)
                dataLoaded.value = true
            }

            Loading()
        } else {
            if (tripViewModel.tripList.observeAsState().value!!.size == 0) {
                showNoTripData.value = true
            } else {
                showNoTripData.value = false
            }
            if (showNoTripData.observeAsState().value!!) {
                NoTrip()
            } else {
                TripList(context = context, tripViewModel = tripViewModel)
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
fun TripList(context: Context,tripViewModel: TripViewModel) {
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(top = 4.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(tripViewModel.tripList.value!!.size) { index ->

            val dismissState = rememberDismissState()

            if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                tripViewModel.tripList.value!!.removeAt(index)
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
                    if (tripViewModel.tripList.value!!.size != 0) {
                        TripListItem(index = index, context = context, tripViewModel = tripViewModel)
                    } else {
                        showNoTripData.value = true
                    }
                }
            )
        }

    }
}
