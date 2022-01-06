@file:OptIn(ExperimentalMaterial3Api::class)

package com.speedtracker.app.screens.walkthrough.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ScaffoldState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.compose.ui.zIndex
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.speedtracker.R
import com.speedtracker.app.screens.mainscreen.speed.ActualSpeedPart
import com.speedtracker.app.screens.mainscreen.speed.AdditionalInfoItem
import com.speedtracker.app.screens.mainscreen.speed.SpeedViewModel
import com.speedtracker.app.screens.mainscreen.StatisticsPage
import com.speedtracker.ui.theme.MainGradientBG
import com.speedtracker.ui.theme.SpeedTrackerComposeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MainScreenView(scope: CoroutineScope, scaffoldState: ScaffoldState,speedViewModel: SpeedViewModel) {
    Column(modifier = Modifier
        .fillMaxSize()) {
        ActualSpeedPart(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .background(brush = MainGradientBG),
            speed = speedViewModel.speed,
            scope = scope,
            scaffoldState = scaffoldState,
            speedViewModel = speedViewModel)

        StatisticsPart(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .background(Color.White))
    }
}

//@Composable
//fun TestPath() {
//
//    Canvas(modifier = Modifier.fillMaxSize()) {
//        drawIntoCanvas {
//            val path = Path()
//            path.addArc(RectF(0f, 100f, 200f, 300f), 270f, 180f)
//            it.nativeCanvas.()
//        }
//    }
//}
@OptIn(ExperimentalPagerApi::class)
@Composable
fun StatisticsPart(modifier: Modifier) {
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
                var itemsList = listOf("Overall distance", "Overll max speed", "Overal average speed")
                StatisticsPage(itemList = itemsList)
            } else {
                var itemsList = listOf("Trip distance", "Trip max speed", "Trip average speed","Trip average altitude")
                StatisticsPage(itemList = itemsList)
            }
        }
    }
}



@Preview()
@Composable
fun DefaultPreview() {
    SpeedTrackerComposeTheme {
        val scope = rememberCoroutineScope()
        val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
//        MainScreenView(MutableLiveData(0f),scope,scaffoldState)
    }
}

@Preview()
@Composable
fun AdditionInfoItemPreview() {
    SpeedTrackerComposeTheme {
        AdditionalInfoItem(Modifier.width(130.dp),R.drawable.satellite_icon, "14/17","satellites")
    }
}