package com.speedtracker.app.screens.trips.triplist

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.speedtracker.R
import com.speedtracker.app.screens.trips.TripViewModel
import com.speedtracker.helper.Formatter
import com.speedtracker.ui.theme.Nunito

@Composable
fun TripListItem(index:Int, context: Context, tripViewModel: TripViewModel,navController: NavHostController) {
//    var filterLocations = tripData.locations
    var tripData = tripViewModel.tripList.value!!.get(index)
    var filterLocations = tripData.locations.filter { location -> location.latitude != 0.0 && location.longitude != 0.0 }
    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(70.dp)
        .background(Color.White),
        elevation = 4.dp,
        shape = RoundedCornerShape(4.dp)
    ) {

        if (filterLocations.size == 0) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.car_icon),
                    contentDescription = "Tripicon",
                    modifier = Modifier
                        .padding(start = 15.dp, end = 15.dp)
                        .size(35.dp)
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Corrupted locations swipe to remove",
                        color = Color.DarkGray,
                        fontSize = 16.sp,
                        fontFamily = Nunito
                    )
                }
            }
        } else {
            Row(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clickable {
                    navController.navigate("trip-detail")
                    tripViewModel.choosedTrip.value = tripData
                },
                verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.car_icon),
                    contentDescription = "Tripicon",
                    modifier = Modifier
                        .padding(start = 15.dp, end = 15.dp)
                        .size(35.dp)
                )
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = tripData.tripInfo.tripName!!.replaceFirstChar { it.uppercase()},
                            color = Color.DarkGray,
                            fontSize = 16.sp,
                            fontFamily = Nunito,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f) )
                        Text(text = Formatter.formatDate(tripData.tripInfo.tripStartDate!!),
                            modifier = Modifier
                                .padding(start = 15.dp, end = 15.dp),
                            color = Color.Gray,
                            fontSize = 10.sp)
                    }

                    Row(modifier  = Modifier
                        .fillMaxWidth()
                        .padding(end = 15.dp, top = 4.dp)) {
                        Text(text = Formatter.getCityFromLocation(filterLocations.first(), context = context)!!,
                            fontFamily = Nunito,
                            fontSize = 10.sp,
                            color = Color.Gray)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Filled.ArrowForward, contentDescription = "ToIcon", modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = Formatter.getCityFromLocation(filterLocations.last(), context = context)!!,
                            fontFamily = Nunito,
                            fontSize = 10.sp,
                            color = Color.Gray)
                    }
                }
            }
        }
    }
}