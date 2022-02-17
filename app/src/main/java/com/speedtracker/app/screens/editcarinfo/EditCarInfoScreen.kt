package com.speedtracker.app.screens.editcarinfo

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.speedtracker.MainActivity
import com.speedtracker.R
import com.speedtracker.app.screens.walkthrough.WalkthroughViewModel
import com.speedtracker.app.screens.walkthrough.pages.Dropdown
import com.speedtracker.app.screens.walkthrough.pages.TypeYearTextField
import com.speedtracker.model.AppDatabase
import com.speedtracker.model.Car
import com.speedtracker.model.CarInfo
import com.speedtracker.ui.theme.MainGradientBG
import com.speedtracker.ui.theme.MainGradientStartColor
import com.speedtracker.ui.theme.Nunito
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun EditCarInfoScreen(carList:List<Car>, scope: CoroutineScope, walkthroughViewModel: WalkthroughViewModel, context:Context, carInfo: MutableLiveData<CarInfo?>) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.edit_car_info_screen_title), color = Color.White, fontFamily = Nunito) },
                backgroundColor = MainGradientStartColor,
                actions = {
                    IconButton(onClick = {
                        if (walkthroughViewModel.carModelIndex.value != 0 && walkthroughViewModel.carBrandIndex.value != 0) {


                            scope.launch {
                                walkthroughViewModel.updateCarPreferences(context = context, carInfo = carInfo)
                            }


                        }
                        (context as Activity).onBackPressed()
                    }) {
                        Icon(Icons.Default.Done, contentDescription = "BackArrow", tint = Color.White)
                    }
                    IconButton(onClick = {(context as Activity).onBackPressed()}) {
                        Icon(Icons.Default.Close, contentDescription = "BackArrow", tint = Color.White)
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().background(brush = MainGradientBG), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.edit_car_info_page_title),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                color = Color.White)
            Spacer(modifier = Modifier.weight(1f))
            Dropdown(listOfItems = walkthroughViewModel.brandList.observeAsState().value!!,
                itemClick = {
                    if (it == -1) {
                        walkthroughViewModel.modelList.value = listOf(context.getString(R.string.choose_model_title))
                        walkthroughViewModel.carModelIndex.value = 0
                    } else {
                        var models = carList.get(it).models
                        models.add(0,context.getString(R.string.choose_model_title))
                        walkthroughViewModel.modelList.value = models
                        walkthroughViewModel.carModelIndex.value = 0
                    }
                },
                walkthroughViewModel.carBrandIndex
                ,walkthroughViewModel.carBrand)

            Spacer(modifier = Modifier.weight(1f))

            Dropdown(
                listOfItems = walkthroughViewModel.modelList.observeAsState().value!!, itemClick = {
                },
                walkthroughViewModel.carModelIndex,
                walkthroughViewModel.carModel)
            Spacer(modifier = Modifier.weight(1f))

            TypeYearTextField(manufacturedYear = walkthroughViewModel.manufacturedYear, initialText = walkthroughViewModel.manufacturedYear.value.toString())
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
