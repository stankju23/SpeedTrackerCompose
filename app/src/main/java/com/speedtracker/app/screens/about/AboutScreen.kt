@file:OptIn(ExperimentalMaterial3Api::class)

package com.speedtracker.app.screens.about

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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import coil.compose.rememberImagePainter
import com.skydoves.landscapist.glide.GlideImage
import com.speedtracker.R
import com.speedtracker.model.CarInfo
import com.speedtracker.ui.theme.MainGradientEndColor
import com.speedtracker.ui.theme.MainGradientStartColor
import com.speedtracker.ui.theme.Nunito

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

                        GlideImage(
                            modifier = Modifier.fillMaxSize(),
                            imageModel = Uri.parse(carInfo.observeAsState().value!!.carPhoto)
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
            Box(modifier = Modifier.fillMaxWidth().weight(1f))
        }
    }
}