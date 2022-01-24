@file:OptIn(ExperimentalMaterial3Api::class)

package com.speedtracker.app.screens.settings

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.ui.SettingsSwitch
import com.speedtracker.ui.theme.MainGradientEndColor
import com.speedtracker.ui.theme.MainGradientStartColor
import com.speedtracker.ui.theme.Nunito

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
            SettingsSwitch(
                icon = {
                    Icon(imageVector = Icons.Default.BrokenImage , contentDescription = "Theme")
                },
                state = rememberBooleanSettingState(true),
                title = { Text(text = "Dark Theme") },
                onCheckedChange = {
                },
            )
        }
    }
}

@Preview
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen(context = LocalContext.current)
}