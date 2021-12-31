package com.speedtracker.mainscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.speedtracker.R

@Composable
fun StatisticsItem(title:String) {
    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(70.dp)
        .background(Color.White),
        elevation = 4.dp) {

        Row(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = painterResource(id = R.drawable.satellite_icon),
                contentDescription = "Statistics icon",
                modifier = Modifier
                    .padding(start = 15.dp, end = 15.dp)
                    .size(40.dp))
            Column() {
                Text(text = title, color = Color.DarkGray, fontSize = 16.sp)
                Text(text = "Overall distance from first run of the app", color = Color.LightGray, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "1234 km", modifier = Modifier.padding(end = 15.dp))
        }
    }
}

@Preview
@Composable
fun PreviewStatisticsItem() {
    StatisticsItem("Overall Distance")
}