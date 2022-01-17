package com.speedtracker.app.screens.tripscreen

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
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
import java.util.Comparator


@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun TripPage(tripData: TripData,context: Context) {
    Column(modifier = Modifier.fillMaxSize()) {
        val configuration = LocalConfiguration.current
        var mapWidth = configuration.screenWidthDp
        var mapHeight = configuration.screenHeightDp
        GoogleMaps(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) { googleMap ->
                googleMap.setMapStyle(MapStyleOptions(context.resources.getString(R.string.style_json)))
                val trackOptions = PolylineOptions()
                trackOptions.width(20f)
                trackOptions.visible(true)
                tripData.locations.forEach { location: Location ->
                if(location.latitude != 0.0 && location.longitude != 0.0) {
                    trackOptions.add(
                        LatLng(
                            location.latitude,
                            location.longitude
                        )
                    )
                }
            }
            trackOptions.jointType(JointType.ROUND)
            setMapPaddingBotttom(tripData.locations, map =  googleMap, width = mapWidth, height = mapHeight)
            googleMap.addPolyline(trackOptions).color = android.graphics.Color.parseColor("#9eee65")
        }
        Spacer(modifier = Modifier.weight(1f))
    }
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
