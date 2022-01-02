@file:OptIn(ExperimentalMaterial3Api::class)

package com.speedtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.speedtracker.helper.NavDrawerItem
import com.speedtracker.mainscreen.SpeedViewModel
import com.speedtracker.pages.MainScreenView
import com.speedtracker.ui.theme.MainGradientBG
import com.speedtracker.ui.theme.SpeedTrackerComposeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    val speedViewModel by viewModels<SpeedViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpeedTrackerComposeTheme {
                val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
                val scope = rememberCoroutineScope()
                val navController = rememberNavController()
                // A surface container using the 'background' color from the theme
                Scaffold(
                    scaffoldState = scaffoldState,
                    drawerGesturesEnabled = false,
                    // scrimColor = Color.Red,  // Color for the fade background when you open/close the drawer
                    drawerContent = {
                        Box(modifier = Modifier.fillMaxHeight().width(200.dp)) {
                            Drawer(scope = scope, scaffoldState = scaffoldState, navController = navController)
                        }
                    },
                ) {
                    Navigation(navController = navController,scope,scaffoldState)
                }

            }
        }
    }

    @Composable
    fun Drawer(scope: CoroutineScope, scaffoldState: ScaffoldState, navController: NavController) {
        val items = listOf(
            NavDrawerItem.Settings
        )
        Column(modifier = Modifier
            .background(brush = MainGradientBG)
            .width(200.dp)) {
            // Header
            Image(
                painter = painterResource(id = R.drawable.altitude_icon),
                contentDescription = R.drawable.altitude_icon.toString(),
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .padding(10.dp)
            )
            // Space between
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
            )
            // List of navigation items
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            items.forEach { item ->
                DrawerItem(item = item, selected = currentRoute == item.route, onItemClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                    // Close drawer
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                })
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }

    @Preview(showBackground = false)
    @Composable
    fun DrawerPreview() {
        val scope = rememberCoroutineScope()
        val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
        val navController = rememberNavController()
        Drawer(scope = scope, scaffoldState = scaffoldState, navController = navController)
    }

    @Composable
    fun DrawerItem(item: NavDrawerItem, selected: Boolean, onItemClick: (NavDrawerItem) -> Unit) {
        val background = if (selected) R.color.teal_700 else android.R.color.transparent
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { onItemClick(item) })
                .height(45.dp)
                .background(colorResource(id = background))
                .padding(start = 10.dp)
        ) {
            Image(
                painter = painterResource(id = item.icon),
                contentDescription = item.title,
                colorFilter = ColorFilter.tint(Color.White),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(35.dp)
                    .width(35.dp)
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = item.title,
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }

    @Preview(showBackground = false)
    @Composable
    fun DrawerItemPreview() {
        DrawerItem(item = NavDrawerItem.SpeedMeter, selected = false, onItemClick = {})
    }

    @Composable
    fun Navigation(navController: NavHostController, scope: CoroutineScope, scaffoldState: ScaffoldState) {
        NavHost(navController, startDestination = NavDrawerItem.SpeedMeter.route) {
            composable(NavDrawerItem.SpeedMeter.route) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreenView(speedViewModel.speed,scope,scaffoldState,speedViewModel)
                }
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


}
