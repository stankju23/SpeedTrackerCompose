@file:OptIn(ExperimentalMaterial3Api::class)

package com.speedtracker

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.speedtracker.helper.NavDrawerItem
import com.speedtracker.ui.theme.MainGradientBG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.w3c.dom.Text

open class DrawerView : ComponentActivity() {
    @Composable
    fun Drawer(scope: CoroutineScope, scaffoldState: ScaffoldState, navController: NavController) {
        val items = listOf(
            NavDrawerItem.TripList,
            NavDrawerItem.HeadUpDisplay,
            NavDrawerItem.Settings,
            NavDrawerItem.About

        )
        val configuration = LocalConfiguration.current

        val screenWidth = configuration.screenWidthDp.dp
        val drawerWidth = screenWidth/5*4

        Column(
            modifier = Modifier.width(drawerWidth)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .background(brush = MainGradientBG)
                    .padding(top = 40.dp)
            ) {
                Box(modifier = Modifier.width(drawerWidth)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Your Garage",
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Icon(
                        painter = painterResource(
                            id = R.drawable.edit_icon
                        ),
                        contentDescription = "This is edit icon.",
                        modifier = Modifier
                            .size(30.dp)
                            .align(Alignment.TopEnd)
                            .padding(end = 10.dp),
                        tint = Color.White
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, top = 20.dp, bottom = 20.dp)
                ) {

                        IconButton(
                            modifier = Modifier
                                .size(75.dp)
                                .border(2.dp, color = Color.White, CircleShape),
                            onClick = {}) {
                            Icon(
                                painter = painterResource(id = R.drawable.satellite_icon),
                                contentDescription = "Car icon",
                                modifier = Modifier
                                    .size(75.dp)
                                    .clip(CircleShape)
                            )
                    }
                    Row(
                        modifier = Modifier
                            .weight(3f)
                            .height(75.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(2f)
                                .padding(start = 10.dp)
                        ) {
                            CarInfoText(
                                modifier = Modifier.fillMaxWidth(),
                                title = "Car Brand",
                                message = "Skoda",
                                textAlign = TextAlign.Start
                            )
                            CarInfoText(
                                modifier = Modifier.fillMaxWidth(),
                                title = "Car Model",
                                message = "Octavia Combi",
                                textAlign = TextAlign.Start
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Spacer(modifier = Modifier.height(37.5.dp))
                            CarInfoText(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(start = 10.dp),
                                title = "Year",
                                message = "2015",
                                textAlign = TextAlign.Start
                            )
                        }

                    }
                }


            }
            // Space between
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
            )
            // List of navigation items
            items.forEach { item ->
                DrawerItem(item = item, onItemClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
//                        navController.graph.startDestinationRoute?.let { route ->
//                            popUpTo(route) {
//                                saveState = true
//                            }
//                        }
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
            CloseDraweBottomView(modifier = Modifier.width(drawerWidth), scope = scope, scaffoldState = scaffoldState)
        }
    }

    @Composable
    fun CarInfoText(modifier: Modifier, title: String, message: String, textAlign:TextAlign) {
        Column(modifier) {
            Text(
                text = title,
                fontSize = 10.sp,
                color = Color.White,
                textAlign = textAlign
            )
            Text(
                text = message,
                fontSize = 18.sp,
                color = Color.White,
                textAlign = textAlign
            )
        }
    }

    @Preview(showBackground = false)
    @Composable
    fun DrawerViewPreview() {
        val scope = rememberCoroutineScope()
        val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
        val navController = rememberNavController()
        Drawer(scope = scope, scaffoldState = scaffoldState, navController = navController)
    }

    @Composable
    fun DrawerItem(item: NavDrawerItem, onItemClick: (NavDrawerItem) -> Unit) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { onItemClick(item) })
                .height(45.dp)
                .background(Color.Transparent)
                .padding(start = 10.dp)
        ) {
            Image(
                painter = painterResource(id = item.icon),
                contentDescription = item.title,
                colorFilter = ColorFilter.tint(Color.Black),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(30.dp)
                    .width(30.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.title,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }

    @Composable
    fun CloseDraweBottomView(modifier: Modifier,scope: CoroutineScope, scaffoldState: ScaffoldState) {
        Row(modifier = modifier
            .height(50.dp)
            .background(brush = MainGradientBG),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                scope.launch {
                    scaffoldState.drawerState.close()
                }
            }) {
                Icon(Icons.Default.ArrowBack,
                    contentDescription = "Back Icon",
                tint = Color.White)
            }
        }
    }
    @Preview
    @Composable
    fun PreviewCloseDraweBottomView() {
        val scope = rememberCoroutineScope()
        val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
        CloseDraweBottomView(modifier = Modifier.fillMaxWidth(), scope = scope, scaffoldState = scaffoldState)
    }

    @Preview(showBackground = false)
    @Composable
    fun DrawerItemPreview() {
        DrawerItem(item = NavDrawerItem.SpeedMeter, onItemClick = {})
    }
}