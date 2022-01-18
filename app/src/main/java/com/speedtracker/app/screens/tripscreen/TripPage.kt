@file:OptIn(ExperimentalMaterial3Api::class)

package com.speedtracker.app.screens.tripscreen

import android.app.Activity
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.Transformations
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
//import com.google.android.gms.maps.R
import com.google.android.gms.maps.model.*
import com.speedtracker.app.screens.triplist.choosedTrip
import com.speedtracker.model.Location
import com.speedtracker.model.TripData
import com.speedtracker.R
import com.speedtracker.app.screens.triplist.dataLoaded
import com.speedtracker.app.screens.triplist.showNoTripData
import com.speedtracker.helper.Constants
import com.speedtracker.helper.Formatter
import com.speedtracker.helper.GenerallData
import com.speedtracker.ui.theme.MainGradientEndColor
import com.speedtracker.ui.theme.MainGradientStartColor
import com.speedtracker.ui.theme.Nunito
import com.speedtracker.ui.theme.mapsLineColor
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun TripPage(tripData: TripData,context: Context) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { androidx.compose.material.Text(text = tripData.tripInfo.tripName!!, color = Color.White, fontFamily = Nunito) },
                backgroundColor = MainGradientEndColor,
                navigationIcon = {
                    IconButton(onClick = {(context as Activity).onBackPressed()}) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "BackArrow", tint = Color.White)
                    }
                },
            )
        }
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(color = MainGradientEndColor)) {
            val configuration = LocalConfiguration.current
            var mapWidth = configuration.screenWidthDp
            var mapHeight = configuration.screenHeightDp
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                var startAddressList =
                    getAddressFromLocation(location = tripData.locations.first(), context = context)
                var endAddressList =
                    getAddressFromLocation(location = tripData.locations.last(), context = context)
                Address(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, color = MainGradientStartColor.copy(alpha = 0.5f))
                        .padding(start = 20.dp),
                    title = "START",
                    street = "${startAddressList.get(0)} ${startAddressList.get(1)}",
                    city = startAddressList.get(2)
                )
                Address(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, color = MainGradientStartColor.copy(alpha = 0.5f))
                        .padding(start = 20.dp),
                    title = "END",
                    street = "${endAddressList.get(0)} ${endAddressList.get(1)}",
                    city = endAddressList.get(2)
                )
            }

            val trackOptions = PolylineOptions()
            tripData.locations.forEach { location: Location ->
                if (location.latitude != 0.0 && location.longitude != 0.0) {
                    trackOptions.add(
                        LatLng(
                            location.latitude,
                            location.longitude
                        )
                    )
                }
            }

            GoogleMaps(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
            ) { googleMap ->
                googleMap.setMapStyle(MapStyleOptions(context.resources.getString(R.string.style_json)))
                trackOptions.width(20f)
                trackOptions.visible(true)
                trackOptions.jointType(JointType.ROUND)
                setMapPaddingBotttom(
                    tripData.locations,
                    map = googleMap,
                    width = mapWidth,
                    height = mapHeight
                )
                googleMap.addPolyline(trackOptions).color =
                    android.graphics.Color.parseColor("#9eee65")
            }



            Column(modifier = Modifier
                .weight(0.5f)
                .fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    MapStatisticsItem(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(1.dp, color = MainGradientStartColor.copy(alpha = 0.5f)),
                        image = R.drawable.ic_distance,
                        value = if (GenerallData.isMetric.observeAsState().value!!) "${
                            (Math.round(
                                (Formatter.calculateTripDistance(
                                    trackOptions.points
                                ) / Constants.mToKm) * 10.0
                            ) / 10.0)
                        }" else "${(Math.round((Formatter.calculateTripDistance(trackOptions.points) / Constants.mToMil) * 10.0) / 10.0)}",
                        description = "Distance",
                        unit = if (GenerallData.isMetric.value!!) "km" else "mil"
                    )


                    MapStatisticsItem(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(1.dp, color = MainGradientStartColor.copy(alpha = 0.5f)),
                        image = R.drawable.ic_avgspeed,
                        value = if (GenerallData.isMetric.observeAsState().value!!) if (tripData.tripInfo.countOfUpdates == 0) "0.0" else "${(tripData.tripInfo.sumOfTripSpeed / tripData.tripInfo.countOfUpdates / Constants.msToKmh).toInt()}" else if (tripData.tripInfo.countOfUpdates == 0) "0.0" else "${(tripData.tripInfo.sumOfTripSpeed / tripData.tripInfo.countOfUpdates / Constants.msToMph).toInt()}",
                        description = "Average speed",
                        unit = if (GenerallData.isMetric.value!!) "km/h" else "mph"
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    MapStatisticsItem(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(1.dp, color = MainGradientStartColor.copy(alpha = 0.5f)),
                        image = R.drawable.ic_topspeed,
                        value = if (GenerallData.isMetric.observeAsState().value!!) "${(tripData.tripInfo.maxSpeed / Constants.msToKmh).toInt()}" else "${(tripData.tripInfo.maxSpeed / Constants.msToMph).toInt()}",
                        description = "Top Speed",
                        unit = if (GenerallData.isMetric.value!!) "km/h" else "mph"
                    )
                    MapStatisticsItem(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(1.dp, color = MainGradientStartColor.copy(alpha = 0.5f)),
                        image = R.drawable.trip_time,
                        value = "${
                            Formatter.calculateTripTime(
                                Date(tripData.tripInfo.tripStartDate!!),
                                Date(tripData.tripInfo.tripEndDate!!)
                            )
                        }",
                        description = "Time",
                        unit = "h"
                    )
                }
            }
            }
        }
    }

@Composable
fun MapStatisticsItem(modifier: Modifier,image:Int,value:String, description:String,unit:String) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        Icon(modifier = Modifier
            .size(50.dp)
            .padding(top = 10.dp, bottom = 10.dp, end = 10.dp), painter = painterResource(id = image), contentDescription = "", tint = Color.White)
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Row() {
                Text(text = value, color = Color.White, fontSize = 25.sp, fontFamily = Nunito,modifier = Modifier.alignByBaseline())
                Text(text = unit, color = Color.White, fontSize = 10.sp, fontFamily = Nunito, modifier = Modifier.alignByBaseline().padding(start = 5.dp))
            }
            Text(text = description, color = Color.LightGray, fontSize = 10.sp, fontFamily = Nunito)
        }
    }
}



@Preview
@Composable
fun PreviewMapStatisticsItem() {
    MapStatisticsItem(modifier = Modifier.size(170.dp),R.drawable.ic_distance, "152.6", "Distance","km")
}
fun getAddressFromLocation(location: Location,context: Context) :List<String>{
    var geocoder = Geocoder(context, Locale.getDefault())
    var addresses:List<Address>? = null
    try {
        addresses = geocoder.getFromLocation(
            location.latitude,
            location.longitude,
            10
        )
    } catch (e: Exception) {
        Log.e("Geocoder error ", e.localizedMessage)
    }

    var addressList = ArrayList<String>()
    if (addresses != null && addresses.get(0).thoroughfare != null) {
        addressList.add(addresses.get(0).thoroughfare)
    }

    var locality: Address?
    if (addresses != null && addresses.get(0).featureName != null) {
        addressList.add(addresses.get(0).featureName)
        locality = addresses.firstOrNull { address -> address.locality != null }
        if (locality != null) {

            addressList.add(locality.locality)
            if (addressList.size == 2) {
                addressList.add(0,locality.locality)
            }
        } else {
            addressList.add("Unknown")
        }
    }
    return addressList
}

@RequiresApi(Build.VERSION_CODES.N)
private fun setMapPaddingBotttom(locations:List<Location>, map: GoogleMap,width:Int,height:Int) {
    Log.i("Map padding", "called")

    var north = locations.maxWithOrNull(Comparator.comparingDouble { it.latitude })
    var south = locations.minWithOrNull(Comparator.comparingDouble { it.latitude })
    var east = locations.maxWithOrNull(Comparator.comparingDouble { it.longitude })
    var west = locations.minWithOrNull(Comparator.comparingDouble { it.longitude })

//        var centerZoomLocation = trip.locations.first { it.latitude != 0.0 && it.longitude != 0.0 }
//        val center = LatLng(
//            centerZoomLocation.latitude, centerZoomLocation.longitude
//        )
    val builder = LatLngBounds.Builder()
//        builder.include(center)
    builder.include(LatLng(north!!.latitude, north.longitude))
    builder.include(LatLng(south!!.latitude, south.longitude))
    builder.include(LatLng(east!!.latitude, east.longitude))
    builder.include(LatLng(west!!.latitude, west.longitude))

    val bounds = builder.build()

//        val height = (resources.displayMetrics.heightPixels - (Math.round(offset * 0.2))).toFloat() / resources.displayMetrics.heightPixels.toFloat()
    val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, 0)

    map.setOnMapLoadedCallback(object : GoogleMap.OnMapLoadedCallback {
        override fun onMapLoaded() {
            map.animateCamera(cu)
        }
    })
}

@Preview
@Composable
fun AdressPreview() {
    Row(modifier = Modifier
        .fillMaxWidth()) {
        Address(modifier = Modifier
            .weight(1f)
            .border(1.dp, color = MainGradientStartColor.copy(alpha = 0.5f))
            .padding(start = 20.dp), title = "START", street = "CSA 43", city =  "Kysucne Nove Mesto")
        Address(modifier = Modifier
            .weight(1f)
            .border(1.dp, color = MainGradientStartColor.copy(alpha = 0.5f))
            .padding(start = 20.dp),  title = "END", street = "Pecenice 73", city =  "Pecenice")
    }

}

@Composable
fun Address(modifier: Modifier,title:String, street:String, city:String) {
    Column(modifier = modifier.padding(top = 20.dp, bottom = 20.dp)) {
        Text(modifier = Modifier.fillMaxWidth(), text = title, color = mapsLineColor, fontFamily = Nunito, fontSize = 12.sp)
        Text(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),text = street, color = Color.White, fontFamily = Nunito,fontSize = 14.sp)
        Text(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp),text = city, color = Color.White, fontFamily = Nunito,fontSize = 14.sp)
    }
}

@Composable
fun GoogleMaps(
    modifier: Modifier = Modifier,
    onReady:(GoogleMap)-> Unit
) {

    val context = LocalContext.current

    val mapView = remember {
        MapView(context)
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    lifecycle.addObserver(rememberMapLifecycleObserver(mapView = mapView))

    AndroidView(
        factory ={
            mapView.apply {
                mapView.getMapAsync { googleMap ->
                    onReady(googleMap)
                }
            }
    },
    modifier = modifier)

}

//@Composable
//fun GoogleMapSnapshot(location: LatLng) {
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(200.dp)
//    ) {
//        val mapView = rememberMapViewWithLifecycle()
//
//        MapViewContainer(
//            map = mapView,
//            location = location
//        )
//    }
//}
//
//@Composable
//private fun MapViewContainer(
//    map: MapView,
//    location: LatLng
//) {
//    val coroutineScope = rememberCoroutineScope()
//
//    AndroidView({ map }) { mapView ->
//        coroutineScope.launch {
//            val googleMap = mapView.awaitMap()
//            val zoom = calculateZoom(cameraPosition)
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom))
//            googleMap.addMarker { position(cameraPosition) }
//        }
//    }
//}
//
//@Composable
//fun rememberMapViewWithLifeCycle(): MapView {
//    val context = LocalContext.current
//    val mapView = remember {
//        MapView(context).apply {
//            id = com.google.maps.android.ktx.R.id.map_frame
//        }
//    }
//    val lifeCycleObserver = rememberMapLifecycleObserver(mapView)
//    val lifeCycle = LocalLifecycleOwner.current.lifecycle
//    DisposableEffect(lifeCycle) {
//        lifeCycle.addObserver(lifeCycleObserver)
//        onDispose {
//            lifeCycle.removeObserver(lifeCycleObserver)
//        }
//    }
//
//    return mapView
//}
//
@Composable
fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            when(event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> throw IllegalStateException()
            }
        }
    }
