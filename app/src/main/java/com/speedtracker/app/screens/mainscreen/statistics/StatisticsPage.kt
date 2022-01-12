package com.speedtracker.app.screens.mainscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.speedtracker.R
import com.speedtracker.app.screens.mainscreen.statistics.Statistic
import com.speedtracker.helper.GenerallData

@Composable
fun StatisticsPage(statisticList: List<Statistic>) {
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(statisticList.size) { index ->
            StatisticsItem(statistic = statisticList.get(index) )
        }
    }
}

@Preview
@Composable
fun PreviewStatisticsPage() {
    var itemsList = listOf(Statistic(iconDrawable = R.drawable.ic_avgspeed,name = "Overall max speed", value = MutableLiveData("1.2"),MutableLiveData("km/h")))
    StatisticsPage(statisticList = itemsList)
}

