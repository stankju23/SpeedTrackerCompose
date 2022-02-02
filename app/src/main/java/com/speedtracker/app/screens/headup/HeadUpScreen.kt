package com.speedtracker.app.screens.headup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.speedtracker.R
import com.speedtracker.app.screens.components.LoadingComponent
import com.speedtracker.app.screens.mainscreen.speed.SpeedViewModel
import com.speedtracker.helper.Constants
import com.speedtracker.helper.GenerallData

@Composable
fun HeadUpScreen(speedViewModel: SpeedViewModel) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {
        Row(modifier = Modifier
            .rotate(-90f)
            .scale(scaleY = 1f, scaleX = -1f)
            .align(Alignment.Center)) {
            Text(text = "${speedViewModel.speed.observeAsState().value!!} ", color = Color.White, fontSize = 160.sp, modifier = Modifier.alignByBaseline())
            Text(text = if(GenerallData.isMetric.value!!) stringResource(id = R.string.speed_units_metric) else stringResource(id = R.string.speed_units_imperial), color = Color.White, fontSize = 40.sp, modifier = Modifier
                .alignByBaseline()
                .padding(start = 16.dp))
        }

        if (speedViewModel.searchingForGPSLocation.observeAsState().value == true) {
            LoadingComponent(modifier = Modifier
                .height(60.dp)
                .align(Alignment.CenterEnd)
                .rotate(-90f)
                .scale(scaleY = 1f, scaleX = -1f))
        }

    }
}

@Preview
@Composable
fun PreviewHeadUpScreen() {
    HeadUpScreen(speedViewModel = SpeedViewModel())
}