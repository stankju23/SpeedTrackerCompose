@file:OptIn(ExperimentalMaterial3Api::class)

package com.speedtracker.pages

import android.graphics.Path
import android.graphics.RectF
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScaffoldState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.MutableLiveData
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.speedtracker.R
import com.speedtracker.mainscreen.SpeedViewModel
import com.speedtracker.mainscreen.StatisticsPage
import com.speedtracker.ui.theme.MainGradientBG
import com.speedtracker.ui.theme.MainGradientEndColor
import com.speedtracker.ui.theme.SpeedTrackerComposeTheme
import com.speedtracker.ui.theme.Typography
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MainScreenView(speed:MutableLiveData<Float>, scope: CoroutineScope, scaffoldState: ScaffoldState,speedViewModel:SpeedViewModel) {
    Column(modifier = Modifier
        .fillMaxSize()) {
        ActualSpeedPart(modifier = Modifier
            .weight(10f)
            .fillMaxWidth()
            .background(brush = MainGradientBG),
            speed = speed,
            scope = scope,
            scaffoldState = scaffoldState,
            speedViewModel = speedViewModel)

        StatisticsPart(modifier = Modifier
            .weight(9f)
            .fillMaxWidth()
            .background(Color.White))
    }
}

@Composable
fun ActualSpeedPart(modifier: Modifier,speed:MutableLiveData<Float>, scope: CoroutineScope, scaffoldState: ScaffoldState,speedViewModel:SpeedViewModel) {
    Column(modifier = modifier) {
        ActualSpeedPartTopBar(scope,scaffoldState, speedViewModel = speedViewModel)
        Column(modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.Center) {
            SpeedText(modifier = Modifier
                .fillMaxWidth()
                .weight(3f)
                .align(Alignment.CenterHorizontally), speed = speed)
            AdditionalInfo(modifier = Modifier
                .fillMaxWidth()
                .weight(1f))
        }
    }
}

@Composable
fun ActualSpeedPartTopBar(scope: CoroutineScope, scaffoldState: ScaffoldState,speedViewModel:SpeedViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {
            scope.launch {
                scaffoldState.drawerState.open()
        } })
        {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "Menu Btn",
                tint = Color.White)
        }
        Row(modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .width(15.dp)
                    .height(15.dp),
                color = Color.White,
                strokeWidth = 1.5.dp
            )

            Column(modifier = Modifier
                .padding(start = 6.dp),) {
                Text(
                    textAlign = TextAlign.Center,
                    text = "Searching for GPS location...",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

        }
        IconButton(onClick = { speedViewModel.animate0To200And200To0() }) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Menu Btn",
                tint = Color.White)
        }
    }
}

@Composable
fun SpeedText(modifier: Modifier,speed:MutableLiveData<Float>) {

    var speedTextStyle by remember { mutableStateOf(Typography.bodyLarge) }
    var unitTextStyle by remember { mutableStateOf(Typography.titleLarge)}
    var speedTextReadyToDraw by remember { mutableStateOf(false) }
    var unitTextReadyToDraw by remember { mutableStateOf(false) }
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        var needsToResize by remember { mutableStateOf(false) }

        Text(
            text = "${speed.observeAsState().value!!.toInt()}",
            color = Color.White,
            maxLines = 1,
            style = speedTextStyle,
            softWrap = false,
            textAlign = TextAlign.Right,
            modifier = Modifier
                .weight(4f)
                .alignByBaseline()
                .padding(start = 20.dp)
//                .drawWithContent {
//                    if (speedTextReadyToDraw) drawContent()
//                },
//            onTextLayout = { textLayoutResult ->
//                if (textLayoutResult.didOverflowWidth || textLayoutResult.didOverflowHeight) {
//                    speedTextStyle = speedTextStyle.copy(fontSize = speedTextStyle.fontSize * 0.9)
//                    needsToResize = true
//                } else {
////                    if (textLayoutResult.size.width > textLayoutResult.multiParagraph.width || textLayoutResult.size.height > textLayoutResult.multiParagraph.height) {
////                        speedTextStyle = speedTextStyle.copy(fontSize = Typography.bodyLarge.fontSize)
////                    }
////                    }
//                    speedTextReadyToDraw = true
//                }
//            }
    )

        Text(
            text = "km/h",
            color = Color.White,
            style = unitTextStyle,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier
                .weight(1f)
                .alignByBaseline()
                .padding(end = 20.dp),
            onTextLayout = { textLayoutResult ->
                if (textLayoutResult.didOverflowWidth) {
                    unitTextStyle = unitTextStyle.copy(fontSize = unitTextStyle.fontSize * 0.9)
                } else {
                    unitTextReadyToDraw = true
                }
            })
    }
}

@Composable
fun AdditionalInfo(modifier: Modifier) {
    Row(modifier = modifier.padding(bottom = 20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
    AdditionalInfoItem(
        modifier = Modifier.width(150.dp),
        imageRes = R.drawable.satellite_icon,
        value = "14/17",
        units = "satellites"
    )
        AdditionalInfoItem(
            modifier = Modifier.width(120.dp),
            imageRes = R.drawable.altitude_icon,
            value = "6429",
            units = "m.n.m"
        )
//        ProgressBar(
//            progress = 0.8f,
//            activeColor = Brush.linearGradient(listOf(Color.Yellow,Color.Green,Color.Red)),
//            inactiveColor = Color.Gray,
//            modifier = Modifier
//                .fillMaxWidth()
//                .fillMaxHeight(),
//        strokeWidth = 10.dp,
//        paddings = 30.dp)
    }
}

@Composable
fun AdditionalInfoItem(modifier: Modifier,imageRes:Int, value:String, units:String) {

    var valueTextStyle by remember { mutableStateOf(Typography.titleLarge)}

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Image(painter = painterResource(id = imageRes) , contentDescription = "Additional info item image: ${imageRes}", modifier = Modifier
            .size(50.dp)
            .padding(5.dp))
        Column() {
            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
                text = value,
                color = Color.White,
                fontSize = 20.sp,
                maxLines = 1,
                textAlign = TextAlign.Left,
                onTextLayout = { textLayoutResult ->
                    if (textLayoutResult.didOverflowWidth) {
                        valueTextStyle = valueTextStyle.copy(fontSize = valueTextStyle.fontSize * 0.9)
                    }
                })
            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
                text = units,
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Left,)

        }
    }
}

@Composable
fun ProgressBar(
    progress:Float,
    activeColor:Brush,
    inactiveColor:Color,
    modifier: Modifier = Modifier,
    strokeWidth:Dp = 5.dp,
    paddings:Dp = 20.dp
) {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }

    var currentValue by remember {
        mutableStateOf(progress)
    }
    var offset by remember {
        mutableStateOf(paddings*2)
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.onSizeChanged {
            size = IntSize(it.width,it.height)
        }
    ) {
        Canvas(modifier = modifier.padding(paddings)) {
            drawArc(
                topLeft = Offset(0f,-size.height.toFloat()/4f ),
                color = inactiveColor,
                startAngle = 20f,
                sweepAngle = 140f,
                useCenter = false,
                size= Size(size.width.toFloat() - offset.toPx(),size.height.toFloat() - offset.toPx()),
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            drawArc(
                topLeft = Offset(0f,-size.height.toFloat()/4f ),
                brush = activeColor,
                startAngle = 160f,
                sweepAngle = -140 * currentValue,
                useCenter = false,
                size= Size(size.width.toFloat() - offset.toPx(),size.height.toFloat() - offset.toPx()),
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )



//            val center = Offset(size.width/2f,size.height/2f)
//            var beta = (-140f * progress + 160f) * (PI / - 180f).toFloat()
//            val rad = cos(beta) * size.width /2f
//
//            val a = cos(beta) * rad
//            val b = sin(beta) * rad
//
//            drawPoints(
//                listOf(
//                    Offset(center.x + a, center.y + b)
//                ),
//                pointMode = PointMode.Points,
//                color = activeColor,
//                strokeWidth = (strokeWidth * 3).toPx(),
//                cap = StrokeCap.Round
//            )
        }
        Text(text = "0 km/h", modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(bottom = paddings + 20.dp, start = paddings + 5.dp), fontSize = 12.sp, color = Color.White)
        Text(text = "70 km/h", modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = paddings + 20.dp, end = paddings + 5.dp), fontSize = 12.sp, color = Color.White)

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
                            Text(title, fontSize = 16.sp, modifier = Modifier.padding(start = 4.dp))
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