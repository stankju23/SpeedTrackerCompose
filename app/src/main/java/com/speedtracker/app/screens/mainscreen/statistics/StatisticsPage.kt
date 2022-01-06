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

@Composable
fun StatisticsPage(itemList: List<String>) {
    LazyColumn(modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(itemList.size) { index ->
            StatisticsItem(title = itemList.get(index))
        }
    }
}

@Preview
@Composable
fun PreviewStatisticsPage() {
    var itemsList = listOf<String>("Trip distance", "Trip max speed", "Trip average speed")
    StatisticsPage(itemsList)
}

