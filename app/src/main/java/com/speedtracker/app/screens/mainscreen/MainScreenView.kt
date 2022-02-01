@file:OptIn(ExperimentalMaterial3Api::class)

package com.speedtracker.app.screens.walkthrough.pages

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ScaffoldState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.compose.ui.zIndex
import androidx.lifecycle.MutableLiveData
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.speedtracker.DrawerValue
import com.speedtracker.R
import com.speedtracker.app.screens.mainscreen.speed.ActualSpeedPart
import com.speedtracker.app.screens.mainscreen.speed.AdditionalInfoItem
import com.speedtracker.app.screens.mainscreen.speed.SpeedViewModel
import com.speedtracker.app.screens.mainscreen.StatisticsPage
import com.speedtracker.app.screens.mainscreen.statistics.StatisticsViewModel
import com.speedtracker.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun MainScreenView(paddingValues: PaddingValues,scope: CoroutineScope, scaffoldState: MutableState<DrawerValue>, speedViewModel: SpeedViewModel, statisticsViewModel: StatisticsViewModel, context: Context, showTripDialog:MutableLiveData<Boolean>, tripName:MutableLiveData<String>,) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = paddingValues.calculateBottomPadding())) {
        ActualSpeedPart(modifier = Modifier
            .weight(1.2f)
            .fillMaxWidth()
            .background(brush = MainGradientBG),
            speed = speedViewModel.speed,
            scope = scope,
            scaffoldState = scaffoldState,
            speedViewModel = speedViewModel,
            context = context,
            statisticsViewModel = statisticsViewModel,
            showTripDialog = showTripDialog)

        StatisticsPart(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .background(Color.White),
            statisticsViewModel = statisticsViewModel)
    }
    TripDialog(showDialog = showTripDialog, tripName = tripName, context = context, statisticsViewModel = statisticsViewModel, speedViewModel = speedViewModel)
}

@Composable
fun TripDialog(showDialog: MutableLiveData<Boolean>, tripName: MutableLiveData<String>, statisticsViewModel: StatisticsViewModel, context: Context,speedViewModel: SpeedViewModel) {
    val confirmButtonColor = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
    )
    val dismissButtonColor = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
    )
    var isError by rememberSaveable { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }

    if (showDialog.observeAsState().value!!) {
        androidx.compose.material3.AlertDialog(
                onDismissRequest = {
                    showDialog.value = false
                    isError = false
                },
                title = {
                    Text("Start new trip", color = MainGradientStartColor, fontSize = 26.sp)
                },
                text = {
                    Column(modifier = Modifier.height(75.dp)) {
                        TextField(
                                value = text,
                                textStyle = TextStyle(
                                        fontFamily = FontFamily.Default,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 18.sp,
                                        lineHeight = 18.sp,
                                        letterSpacing = 0.5.sp,
                                        color = Color.Black,

                                ),
                                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                                label = {
                                    androidx.compose.material.Text(
                                            text = "Trip name",
                                            color = Color.Black
                                    )
                                },
                                onValueChange = {
                                    text = it
                                    tripName.value = it
                                },
                                trailingIcon = {
                                    if (isError)
                                        Icon(Icons.Filled.Info, "error", tint = androidx.compose.material.MaterialTheme.colors.error)
                                },
                                singleLine = true,
                                isError = isError,
                        )
                        if (isError) {
                            androidx.compose.material.Text(
                                    text = "Trip name cannot be empty",
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                                    style = androidx.compose.material.MaterialTheme.typography.caption,
                                    modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                            colors = confirmButtonColor,
                            onClick = {
                                // Change the state to close the dialog
                                if (tripName.value!!.isEmpty()) {
                                    isError = true
                                } else {
                                    statisticsViewModel.startTrip(tripName = tripName.value!!, context = context)
                                    showDialog.value = false
                                }
                            },
                    ) {
                        Text("Start", color = Color.Black, fontSize = 16.sp)
                    }
                },
                dismissButton = {
                    Button(
                            colors = dismissButtonColor,
                            onClick = {
                                // Change the state to close the dialog
                                showDialog.value = false
                                isError = false
                            },
                    ) {
                        Text("Cancel", color = Color.Black, fontSize = 16.sp)
                    }
                }
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun StatisticsPart(modifier: Modifier,statisticsViewModel: StatisticsViewModel) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {

        val pagerState = rememberPagerState()
        val coroutineScope = rememberCoroutineScope()

        var pages = listOf("General", "Trip")


        Row(Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(1f))
            TabRow(
                modifier = Modifier
                    .weight(4f)
                    .padding(top = 10.dp, bottom = 10.dp, start = 2.dp, end = 2.dp)
                    .clip(RoundedCornerShape(50))
                    .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(50)),

                // Our selected tab is our current page
                selectedTabIndex = pagerState.currentPage,

                // Override the indicator, using the provided pagerTabIndicatorOffset modifier
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier
                            .background(Color.White)
                            .pagerTabIndicatorOffset(pagerState, tabPositions)
                            .fillMaxHeight()
                            .zIndex(1f)
                            .clip(RoundedCornerShape(50))
                        ,
                        color = Color.Gray.copy(alpha = 0.5f)
                    )
                }
            ) {
                // Add tabs for all of our pages
                pages.forEachIndexed { index, title ->
                    Tab(
                        modifier = Modifier
                            .height(35.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Transparent)
                            .zIndex(1.2f) ,
                        selected = pagerState.currentPage == index,
                        text = { Row(verticalAlignment = Alignment.CenterVertically) {
//                            Icon(painter = painterResource(id = R.drawable.trip_icon), contentDescription = "Trip icon",modifier=Modifier.size(25.dp))
                            Text(title, fontSize = 16.sp, modifier = Modifier.padding(start = 4.dp), color = Color.Black)
                        } },
                        onClick = { coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        } },
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }


        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            count = pages.size,
            state = pagerState,
        ) { page ->
            if (page == 0) {
                StatisticsPage(statisticList = statisticsViewModel.overallStatisticsList)
            } else {
                var itemsList = listOf("Trip distance", "Trip max speed", "Trip average speed","Trip average altitude")
                StatisticsPage(statisticList = statisticsViewModel.tripStatisticsList)
            }
        }
    }
}



@Preview()
@Composable
fun AdditionInfoItemPreview() {
    SpeedTrackerComposeTheme {
        AdditionalInfoItem(Modifier.width(130.dp),R.drawable.satellite_icon, MutableLiveData("14/17"),
            MutableLiveData("satellites"))
    }
}