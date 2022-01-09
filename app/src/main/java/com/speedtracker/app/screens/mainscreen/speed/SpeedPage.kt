package com.speedtracker.app.screens.mainscreen.speed

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.speedtracker.R
import com.speedtracker.ui.theme.Typography
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActualSpeedPart(modifier: Modifier, speed: MutableLiveData<Int>, scope: CoroutineScope, scaffoldState: ScaffoldState, speedViewModel: SpeedViewModel) {
    Column(modifier = modifier) {
        ActualSpeedPartTopBar(scope, scaffoldState, speedViewModel = speedViewModel)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            SpeedText(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f)
                    .align(Alignment.CenterHorizontally), speed = speed
            )
            AdditionalInfo(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                speedViewModel = speedViewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActualSpeedPartTopBar(scope: CoroutineScope, scaffoldState: ScaffoldState, speedViewModel: SpeedViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            scope.launch {
                scaffoldState.drawerState.open()
            }
        })
        {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "Menu Btn",
                tint = Color.White
            )
        }
        if (speedViewModel.searchingForGPSLocation.observeAsState().value == true) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .width(15.dp)
                        .height(15.dp),
                    color = Color.White,
                    strokeWidth = 1.5.dp
                )

                Column(
                    modifier = Modifier
                        .padding(start = 6.dp),
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = "Searching for GPS location...",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }

            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
        IconButton(onClick = { speedViewModel.animate0To200And200To0() }) {
            Icon(
                imageVector = Icons.Filled.Add,
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
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {

        Text(
            text = "${speed.observeAsState().value!!.toInt()}",
            color = Color.White,
            maxLines = 1,
            style = speedTextStyle,
            textAlign = TextAlign.Right,
            modifier = Modifier
                .weight(4f)
                .alignByBaseline()
                .padding(start = 10.dp)
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