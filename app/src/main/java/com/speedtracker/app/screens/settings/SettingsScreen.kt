@file:OptIn(ExperimentalMaterial3Api::class)

package com.speedtracker.app.screens.settings

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.speedtracker.ui.theme.MainGradientEndColor
import com.speedtracker.ui.theme.MainGradientStartColor
import com.speedtracker.ui.theme.Nunito
import com.speedtracker.ui.theme.Pink40

var resetApp: MutableLiveData<Boolean> = MutableLiveData(false)

@Composable
fun SettingsScreen(context: Context) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings", color = Color.White, fontFamily = Nunito) },
                backgroundColor = MainGradientStartColor,
                navigationIcon = {
                    IconButton(onClick = {(context as Activity).onBackPressed()}) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "BackArrow", tint = Color.White)
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(text = "General",
                color = Pink40,
                fontSize = 15.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp))
            SettingsItem(title = "Metric system",
                subtitle = "You can choose between metric or imperial unit system. Metric is set by default.",
                rightPart = {
                Switch(modifier = Modifier.padding(start = 20.dp,end = 12.dp),checked = false, onCheckedChange = {})
            })
            Box(modifier = Modifier.fillMaxWidth().height(0.7.dp).background(MainGradientEndColor.copy(alpha = 0.3f)))
            SettingsItem(title = "Reset App", subtitle = "Click to remove all stored data.", rightPart = null)
            Box(modifier = Modifier.fillMaxWidth().height(0.7.dp).background(MainGradientEndColor.copy(alpha = 0.3f)))

        }
    }
}

@Composable
fun SettingsItem(title:String, subtitle:String, rightPart: @Composable ((checked:MutableLiveData<Boolean>) -> Unit)? = null) {
    Row(modifier = Modifier
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier
            .padding(start = 16.dp, top = 12.dp, bottom = 14.dp)
            .weight(1f)) {
            Text(text = title,
                fontSize = 16.sp,
                fontFamily = Nunito,
                color = Color.Black,
                fontWeight = FontWeight.Normal)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle,
                fontSize = 12.sp,
                fontFamily = Nunito,
                color = Color.Gray)
        }
        if (rightPart != null) {
            Box(contentAlignment = Alignment.CenterEnd) {
                rightPart(resetApp)
            }
        }

    }
}


@Preview
@Composable
fun PreviewSettingsItem() {
    SettingsItem(title = "", subtitle = "", rightPart = { checked ->
        Switch(modifier = Modifier.padding(start = 20.dp,end = 12.dp),
            checked = checked.value!!,
            onCheckedChange = {
                checked.value = it
            })
    })
}

@Preview
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen(context = LocalContext.current)
}