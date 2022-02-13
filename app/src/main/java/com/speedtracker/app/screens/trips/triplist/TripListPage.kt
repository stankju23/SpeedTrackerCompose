@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class, ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class
)

package com.speedtracker.app.screens.trips.triplist

import android.app.Activity
import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.speedtracker.R
import com.speedtracker.app.screens.trips.TripViewModel
import com.speedtracker.model.TripData
import com.speedtracker.ui.theme.MainGradientStartColor
import com.speedtracker.ui.theme.Nunito

var dataLoaded:MutableLiveData<Boolean> = MutableLiveData(false)
var showNoTripData:MutableLiveData<Boolean> = MutableLiveData(false)

@Composable
fun TripListPage(paddingValues: PaddingValues,context: Context,tripViewModel: TripViewModel,navController: NavHostController) {
    Scaffold(
        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()) ,
        topBar = {
            TopAppBar(
                title = { Text(text = "Trip List", color = Color.White, fontFamily = Nunito) },
                backgroundColor = MainGradientStartColor
//                navigationIcon = {
//                    IconButton(onClick = {(context as Activity).onBackPressed()}) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "BackArrow", tint = Color.White)
//                        dataLoaded.value = false
//                        showNoTripData.value = false
//                    }
//                },
            )
        }
    ) {


        LaunchedEffect("") {
            dataLoaded.value = false
            tripViewModel.loadTrips(context = context)
            dataLoaded.value = true
        }

        if (!dataLoaded.observeAsState().value!!) {
//            SideEffect {
//
//            }
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
                TripList(context = context, tripViewModel = tripViewModel, navController = navController)
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
fun TripList(context: Context,tripViewModel: TripViewModel,navController: NavHostController) {

    var trips = remember {
        mutableStateListOf<TripData>()
    }
    trips.clear()
    tripViewModel.tripList.value!!.forEach {
        trips.add(it)
    }

    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(top = 4.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {


        itemsIndexed(items = trips,
            key={
                index,item->
            item.hashCode()
        }) { index,item ->

            val state= rememberDismissState(
                confirmStateChange = {
                    if (it==DismissValue.DismissedToStart){
                        tripViewModel.deleteTrip(index = index, context = context)
                        tripViewModel.tripList.value!!.remove(item)
                        trips.remove(item)
                    }
                    true
                }
            )


            SwipeToDismiss(
                modifier = Modifier.animateItemPlacement(),
                dismissThresholds = { FractionalThreshold(0.2f) },
                state = state,
                background = {
                    val alignment = Alignment.CenterEnd
                    val icon = Icons.Default.Delete

                    val scale by animateFloatAsState(
                        if (state.targetValue == DismissValue.Default) 0.75f else 1f
                    )

                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(horizontal = Dp(20f)),
                        contentAlignment = alignment
                    ) {
                        Icon(
                            icon,
                            contentDescription = "Delete Icon",
                            modifier = Modifier.scale(scale)
                        )
                    }
                },
                dismissContent = {
                    if (tripViewModel.tripList.value!!.size != 0) {
                        TripListItem(index = index, context = context, tripViewModel = tripViewModel, navController = navController)
                    } else {
                        showNoTripData.value = true
                    }
                },
                directions = setOf(
                    DismissDirection.EndToStart
                ),
            )
        }

    }
}
