@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package com.speedtracker.app.screens.about

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import coil.compose.rememberImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.speedtracker.model.CarInfo
import com.speedtracker.ui.theme.MainGradientStartColor
import com.speedtracker.ui.theme.Nunito
import java.io.FileNotFoundException
import java.io.IOException


@Throws(FileNotFoundException::class, IOException::class)
fun getBitmap(cr: ContentResolver, url: Uri?): Bitmap {
    val input = cr.openInputStream(url!!)
    val bitmap = BitmapFactory.decodeStream(input)
    input!!.close()
    return bitmap
}

@Composable
fun AboutScreen(paddingValues: PaddingValues,carInfo: MutableLiveData<CarInfo?>) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text(text = "About", color = Color.White, fontFamily = Nunito) },
                backgroundColor = MainGradientStartColor
            )
        },
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MainGradientStartColor)) {
            Column(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {

                            if (carInfo.value != null) {
                                Log.d("Car photo path", carInfo.value!!.carPhoto!!)

                                val uri = carInfo.value!!.carPhoto!!

                                Image(
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    painter = rememberImagePainter(
                                        data  = Uri.parse(uri)  // or ht
                                    ),
                                    contentDescription = ""
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            listOf(
                                                MainGradientStartColor.copy(alpha = 0.4f),
                                                MainGradientStartColor
                                            )
                                        )
                                    )
                            )
                    }
                }


                Box(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)) {
                    CarInfoView(modifier = Modifier.padding(20.dp), carBrand = MutableLiveData(carInfo.observeAsState().value!!.carBrand), carModel = MutableLiveData(carInfo.observeAsState().value!!.carModel))
                }
            }
        }
    }
}


@ExperimentalPermissionsApi
@Composable
fun RequireExternalStoragePermission(
    context: Context
) {

    // Permission state
    val permissionState = rememberPermissionState(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    val permissionGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    if (!permissionGranted) {
        SideEffect {
            permissionState.launchPermissionRequest()
        }
    }

}


@Preview
@Composable
fun PreviewCarInfo() {
    CarInfoView(modifier = Modifier.padding(20.dp), carBrand = MutableLiveData("Skoda"), carModel = MutableLiveData("Octavia Combi"))
}
@Composable
fun CarInfoView(modifier: Modifier,carBrand:MutableLiveData<String>,carModel:MutableLiveData<String>) {
    Column(modifier = modifier) {
        Text(text = carBrand.observeAsState().value!!, fontSize = 12.sp, fontWeight = FontWeight.Normal, fontFamily = Nunito, color = Color.Gray)
        Text(text = carModel.observeAsState().value!!, fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = Nunito, color = Color.White)
    }
}