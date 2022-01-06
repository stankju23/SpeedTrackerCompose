@file:OptIn(ExperimentalMaterial3Api::class)

package com.speedtracker

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.speedtracker.helper.NavDrawerItem
import com.speedtracker.app.screens.mainscreen.speed.SpeedViewModel
import com.speedtracker.app.screens.mainscreen.statistics.StatisticsViewModel
import com.speedtracker.app.screens.walkthrough.WalkthroughViewModel
import com.speedtracker.app.screens.walkthrough.pages.MainScreenView
import com.speedtracker.app.screens.walkthrough.pages.WalkthroughScreen
import com.speedtracker.ui.theme.SpeedTrackerComposeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class MainActivity : DrawerView() {

    val speedViewModel by viewModels<SpeedViewModel>()
    val statisticsViewModel by viewModels<StatisticsViewModel>()
    val walkthroughViewModel by viewModels<WalkthroughViewModel>()

    lateinit var scaffoldState:ScaffoldState
    lateinit var scope:CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpeedTrackerComposeTheme {
                scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
                scope = rememberCoroutineScope()
                val navController = rememberNavController()
                // A surface container using the 'background' color from the theme
                Scaffold(
                    scaffoldState = scaffoldState,
                    drawerGesturesEnabled = false,
                    // scrimColor = Color.Red,  // Color for the fade background when you open/close the drawer
                    drawerContent = {
                            Drawer(scope = scope, scaffoldState = scaffoldState, navController = navController)
                    },
                    drawerShape = customShape()
                ) {
                    Navigation(navController = navController,scope,scaffoldState)
                }

            }
        }
    }

    fun customShape() =  object : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline {
            return Outline.Rounded(RoundRect(0f,0f,size.width/5*4,size.height, topRightCornerRadius = CornerRadius(x = 20f, y = 20f), bottomRightCornerRadius = CornerRadius(x = 20f, y = 20f)) )
        }
    }

    @Composable
    fun Navigation(navController: NavHostController, scope: CoroutineScope, scaffoldState: ScaffoldState) {
        var startDestination = ""
        NavHost(navController, startDestination = startDestination) {
            composable("speed-meter") {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreenView(scope,scaffoldState,speedViewModel)
                }
            }
            composable("walkthrough") {
                WalkthroughScreen(context = this@MainActivity, walkthroughViewModel = walkthroughViewModel, navigationController = navController)
            }
            composable(NavDrawerItem.Settings.route) {
                Column(modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()) {
                    Text(text = "This is Settings Screen")
                }
            }
        }
    }

    override fun onBackPressed() {
        if (scope != null && scaffoldState != null) {
            if (scaffoldState.drawerState.isOpen) {
                scope.launch {
                    scaffoldState.drawerState.close()
                }
            } else {
                super.onBackPressed()
            }
        }
    }
}
