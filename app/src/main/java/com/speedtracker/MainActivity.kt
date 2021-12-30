package com.speedtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import com.speedtracker.mainscreen.SpeedViewModel
import com.speedtracker.pages.MainScreenView
import com.speedtracker.ui.theme.SpeedTrackerComposeTheme

class MainActivity : ComponentActivity() {

    val speedViewModel by viewModels<SpeedViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpeedTrackerComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreenView(speedViewModel.speed)
                }
            }
        }
    }
}
