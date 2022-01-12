package com.speedtracker.app.screens.mainscreen

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.speedtracker.R
import com.speedtracker.app.screens.mainscreen.statistics.Statistic
import com.speedtracker.helper.GenerallData

@Composable
fun StatisticsItem(statistic: Statistic) {
    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(70.dp)
        .background(Color.White),
        elevation = 4.dp) {

        Row(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = painterResource(id = statistic.iconDrawable),
                contentDescription = "Statistics icon",
                modifier = Modifier
                    .padding(start = 15.dp, end = 15.dp)
                    .size(35.dp))
            Column() {
                Text(text = statistic.name, color = Color.DarkGray, fontSize = 16.sp)
                Text(text = "Overall distance from first run of the app", color = Color.LightGray, fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "${statistic.value} ${statistic.units}", modifier = Modifier.padding(end = 15.dp))
        }
    }
}

@Preview
@Composable
fun PreviewStatisticsItem() {
//    StatisticsItem(Statistic(iconDrawable = R.drawable.ic_avgspeed, name = "Trip max speed", MutableLiveData("1.2"), MutableLiveData("km/h")))
}