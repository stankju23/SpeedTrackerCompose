package com.speedtracker.app.screens.trips.tripmap

import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView

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

@Composable
fun rememberMapLifecycleObserver(mapView: MapView?): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            try {
                when(event) {
                    Lifecycle.Event.ON_CREATE -> mapView?.onCreate(Bundle())
                    Lifecycle.Event.ON_START -> mapView?.onStart()
                    Lifecycle.Event.ON_RESUME -> mapView?.onResume()
                    Lifecycle.Event.ON_PAUSE -> mapView?.onPause()
                    Lifecycle.Event.ON_STOP -> mapView?.onStop()
                    Lifecycle.Event.ON_DESTROY -> mapView?.onDestroy()
                    else -> throw IllegalStateException()
                }
            } catch (e:Exception) {
                Log.e("Map error", e.localizedMessage)
            }
        }
    }
