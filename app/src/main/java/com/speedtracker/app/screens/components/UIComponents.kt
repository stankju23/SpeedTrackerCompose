package com.speedtracker.app.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoadingComponent(modifier: Modifier) {
    Row(
        modifier = modifier,
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
}