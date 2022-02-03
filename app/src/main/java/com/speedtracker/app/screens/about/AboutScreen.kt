@file:OptIn(ExperimentalMaterial3Api::class)

package com.speedtracker.app.screens.about

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
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
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)) {
                    CarInfoView(modifier = Modifier.padding(20.dp), carBrand = MutableLiveData(carInfo.observeAsState().value!!.carBrand), carModel = MutableLiveData(carInfo.observeAsState().value!!.carModel))
                }
            }
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