package com.speedtracker.app.screens.mainscreen.speed

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.MutableLiveData
import com.justwatter.app.helper.AppDataStoreImpl
import com.speedtracker.DrawerValue
import com.speedtracker.R
import com.speedtracker.app.screens.components.AutoSizeText
import com.speedtracker.app.screens.components.LoadingComponent
import com.speedtracker.app.screens.mainscreen.statistics.StatisticsViewModel
import com.speedtracker.helper.Constants
import com.speedtracker.helper.GenerallData
import com.speedtracker.ui.theme.Typography
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Preview
@Composable
fun previewActualSpeedFun() {
    ActualSpeedPart(
        context = LocalContext.current,
        modifier = Modifier.height(280.dp),
        speed = MutableLiveData(10),
        speedViewModel = SpeedViewModel(),
        statisticsViewModel = StatisticsViewModel(AppDataStoreImpl(LocalContext.current)),
        showTripDialog = MutableLiveData(false)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActualSpeedPart(context: Context, modifier: Modifier, speed: MutableLiveData<Int>, speedViewModel: SpeedViewModel, statisticsViewModel: StatisticsViewModel, showTripDialog:MutableLiveData<Boolean>) {
    Column(modifier = modifier) {
        ActualSpeedPartTopBar(context = context, speedViewModel = speedViewModel, showTripDialog = showTripDialog, statisticsViewModel = statisticsViewModel)
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            SpeedText(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(7f)
                    .align(Alignment.CenterHorizontally), speed = speed
            )
//            Spacer(modifier = Modifier.weight(1f))
            AdditionalInfo(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f),
                speedViewModel = speedViewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActualSpeedPartTopBar(context: Context, speedViewModel: SpeedViewModel,statisticsViewModel: StatisticsViewModel,showTripDialog: MutableLiveData<Boolean>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
//        IconButton(onClick = {
//            scope.launch {
//                scaffoldState.value = DrawerValue.Open
//            }
//        })
//        {
//            Icon(
//                imageVector = Icons.Filled.Menu,
//                contentDescription = "Menu Btn",
//                tint = Color.White
//            )
//        }
        if (speedViewModel.searchingForGPSLocation.observeAsState().value == true) {
            LoadingComponent(modifier = Modifier
                .weight(1f)
                .fillMaxHeight())
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
        IconButton(onClick = {
//            speedViewModel.speed.value = speedViewModel.speed.value!! + 30
//            if (scaffoldState.value == DrawerValue.Closed) {
                if (statisticsViewModel.trip.value == null) {
                    showTripDialog.value = true
                } else {
                    statisticsViewModel.closeTrip(context = context)
                }
//            }
        }) {
            Icon(
                painter = if (statisticsViewModel.trip.observeAsState().value != null) painterResource(
                    id = R.drawable.ic_end_trip) else painterResource(id = R.drawable.ic_start_trip),
                contentDescription = "Menu Btn",
                tint = Color.White
            )
        }
    }
}

@Composable
fun SpeedText(modifier: Modifier, speed: MutableLiveData<Int>) {

    var speedTextStyle by remember { mutableStateOf(Typography.bodyLarge) }
    var unitTextStyle by remember { mutableStateOf(Typography.titleLarge) }
    var speedTextReadyToDraw by remember { mutableStateOf(false) }
    var unitTextReadyToDraw by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {

        Row() {
            AutoSizeText(
                text = "${speed.observeAsState().value!!}",
                color = Color.White,
                style = speedTextStyle,
                minTextSizeSp = 50f,
                maxTextSizeSp = 200f,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .background(Color.Transparent)
                    .alignByBaseline()

    //                .drawWithContent {
    //                    if (speedTextReadyToDraw) drawContent()
    //                },
//                onTextLayout = { textLayoutResult ->
//                    if (textLayoutResult.didOverflowWidth || textLayoutResult.didOverflowHeight) {
//                        speedTextStyle = speedTextStyle.copy(fontSize = speedTextStyle.fontSize * 0.9)
//                        needsToResize = true
//                    } else {
    ////                    if (textLayoutResult.size.width > textLayoutResult.multiParagraph.width || textLayoutResult.size.height > textLayoutResult.multiParagraph.height) {
    ////                        speedTextStyle = speedTextStyle.copy(fontSize = Typography.bodyLarge.fontSize)
    ////                    }
    ////                    }
    //                    speedTextReadyToDraw = true
    //                }
    //            }
            )


            Text(
                text = if(GenerallData.isMetric.value!!) stringResource(id = R.string.speed_units_metric) else stringResource(id = R.string.speed_units_imperial),
                color = Color.White,
                style = unitTextStyle,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .alignByBaseline()
                    .padding(end = 20.dp),
    //            onTextLayout = { textLayoutResult ->
    //                if (textLayoutResult.didOverflowWidth) {
    //                    unitTextStyle = unitTextStyle.copy(fontSize = unitTextStyle.fontSize * 0.9)
    //                } else {
    //                    unitTextReadyToDraw = true
    //                }
    //            }
              )
        }
    }
}

@Composable
fun AdditionalInfo(modifier: Modifier, speedViewModel: SpeedViewModel) {
    Log.d("Satelites", "${speedViewModel.satellitesText.observeAsState().value}")
    Row(
        modifier = modifier.padding(bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        AdditionalInfoItem(
            modifier = Modifier.width(150.dp),
            imageRes = R.drawable.satellite_icon,
            value = MutableLiveData("${speedViewModel.satellitesText.observeAsState().value}"),
            units = MutableLiveData("satellites")
        )
        AdditionalInfoItem(
            modifier = Modifier.width(120.dp),
            imageRes = R.drawable.altitude_icon,
            value = MutableLiveData("${speedViewModel.altitude.observeAsState().value}"),
            units = MutableLiveData("m.n.m")
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
fun AdditionalInfoItem(modifier: Modifier, imageRes:Int, value:MutableLiveData<String>, units:MutableLiveData<String>) {

    var valueTextStyle by remember { mutableStateOf(Typography.titleLarge) }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Additional info item image: ${imageRes}",
            modifier = androidx.compose.ui.Modifier
                .size(50.dp)
                .padding(5.dp)
        )
        Column() {
            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
                text = value.observeAsState().value!!,
                color = Color.White,
                fontSize = 20.sp,
                maxLines = 1,
                textAlign = TextAlign.Left,
                onTextLayout = { textLayoutResult ->
                    if (textLayoutResult.didOverflowWidth) {
                        valueTextStyle =
                            valueTextStyle.copy(fontSize = valueTextStyle.fontSize * 0.9)
                    }
                })
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                text = units.observeAsState().value!!,
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Left,
            )

        }
    }
}

@Composable
fun ProgressBar(
    progress:Float,
    activeColor: Brush,
    inactiveColor: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 5.dp,
    paddings: Dp = 20.dp
) {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }

    var currentValue by remember {
        mutableStateOf(progress)
    }
    var offset by remember {
        mutableStateOf(paddings * 2)
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.onSizeChanged {
            size = IntSize(it.width, it.height)
        }
    ) {
        Canvas(modifier = modifier.padding(paddings)) {
            drawArc(
                topLeft = Offset(0f, -size.height.toFloat() / 4f),
                color = inactiveColor,
                startAngle = 20f,
                sweepAngle = 140f,
                useCenter = false,
                size = Size(
                    size.width.toFloat() - offset.toPx(),
                    size.height.toFloat() - offset.toPx()
                ),
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            drawArc(
                topLeft = Offset(0f, -size.height.toFloat() / 4f),
                brush = activeColor,
                startAngle = 160f,
                sweepAngle = -140 * currentValue,
                useCenter = false,
                size = Size(
                    size.width.toFloat() - offset.toPx(),
                    size.height.toFloat() - offset.toPx()
                ),
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
        Text(
            text = "0 km/h",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = paddings + 20.dp, start = paddings + 5.dp),
            fontSize = 12.sp,
            color = Color.White
        )
        Text(
            text = "70 km/h",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = paddings + 20.dp, end = paddings + 5.dp),
            fontSize = 12.sp,
            color = Color.White
        )

    }
}