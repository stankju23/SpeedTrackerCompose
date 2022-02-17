@file:OptIn(ExperimentalPagerApi::class)

package com.speedtracker.app.screens.walkthrough.pages

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.speedtracker.MainActivity
import com.speedtracker.R
import com.speedtracker.app.screens.mainscreen.statistics.StatisticsViewModel
import com.speedtracker.app.screens.settings.SettingsViewModel
import com.speedtracker.app.screens.walkthrough.WalkthroughViewModel
import com.speedtracker.helper.AssetsHelper
import com.speedtracker.model.AppDatabase
import com.speedtracker.model.CarInfo
import com.speedtracker.ui.theme.MainGradientBG
import com.speedtracker.ui.theme.MainGradientEndColor
import com.speedtracker.ui.theme.MainGradientStartColor
import kotlinx.coroutines.launch
import java.util.*


@Composable
fun WalkthroughScreen(context: Context,walkthroughViewModel: WalkthroughViewModel,navigationController: NavHostController,statisticsViewModel: StatisticsViewModel,settingsViewModel: SettingsViewModel,carInfo:MutableLiveData<CarInfo?>) {
    val mainButtonColor = ButtonDefaults.buttonColors(
        containerColor = MainGradientStartColor,
        contentColor = MaterialTheme.colorScheme.surface
    )

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(brush = MainGradientBG)) {



        val pagerState = rememberPagerState()
        // Display 10 items
        HorizontalPager(
            modifier = Modifier.weight(6f),
            count = 2,
            state = pagerState)
        { page ->
            // Our page content
            when(page) {
                0 -> {
                    CarBrandModelPage(context = context,AssetsHelper.parseCarsBrands(context),walkthroughViewModel = walkthroughViewModel)
                }
                1 -> {
                    AddMoreCarInfo(manufacturedYear = walkthroughViewModel.manufacturedYear, context = context, imageLiveData = walkthroughViewModel.carImage)
                }
            }
        }

        HorizontalPagerIndicator(
            activeColor = Color.White,
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
                .height(10.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {

            val scope = rememberCoroutineScope()
            OutlinedButton(
                onClick = {
                    if (pagerState.currentPage == 1) {
                        var moveToApp = walkthroughViewModel.storeCarPreferences(scope = scope, context = context)
                        if (moveToApp) {
                            // nav to speed view
                            navigationController.popBackStack()
                            statisticsViewModel.initializeStatisticsData(context = context, settingsViewModel = settingsViewModel)
                            var carInfoToStore = CarInfo(carModel = walkthroughViewModel.carModel.value!!,
                                carBrand =  walkthroughViewModel.carBrand.value!!,
                                carIdentifier = UUID.randomUUID().toString(),
                                carManufacturedYear = walkthroughViewModel.manufacturedYear.value.toString(),
                                carPhoto = if (walkthroughViewModel.carImage.value != null) walkthroughViewModel.carImage.value!! else null,
                                id = Calendar.getInstance().time.time.toInt())
                            carInfo.value = carInfoToStore
                            scope.launch {
                                AppDatabase.getDatabase(context = context).carInfoDao().insertCarInfo(carInfo = carInfoToStore)
                            }
//                            navigationController.navigate(route = "speed-meter")
                            (context as MainActivity).showBottomView.value = true

                        }
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }

                     },
                border = BorderStroke(2.dp, Color.White),
                shape = RoundedCornerShape(50), // = 50% percent
                //or shape = CircleShape
                colors = mainButtonColor
            ){
                Text(
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                    text = "Next",
                    fontSize = 18.sp,
                    color = Color.White)
            }
        }

        ErrorsDialog(showDialog = walkthroughViewModel.showErrorDialog, erros = walkthroughViewModel.errors)
    }
}

@Composable
fun ErrorsDialog(showDialog: MutableLiveData<Boolean>,erros:List<WalkthroughViewModel.Errors>) {
    var dialogMessage = "${stringResource(id = R.string.missing_prefs_dialog_mess)} \n"
    erros.forEach {
        dialogMessage += "${stringResource(id = it.textId)}\n"
    }
    val mainButtonColor = ButtonDefaults.buttonColors(
        containerColor = MainGradientEndColor,
        contentColor = MaterialTheme.colorScheme.surface
    )
    if (showDialog.observeAsState().value!!) {
        AlertDialog(
            onDismissRequest = {
            },
            title = {
                Text(stringResource(R.string.walkthrough_error_dialog_title), color = MainGradientStartColor)
            },
            confirmButton = {
                Button(
                    colors = mainButtonColor,
                    onClick = {
                        // Change the state to close the dialog
                        showDialog.value = false
                    },
                ) {
                    Text(stringResource(R.string.dialog_ok_btn))
                }
            },
            text = {
                Text(text = dialogMessage, color = MainGradientStartColor)
            },
        )
    }
}

@Preview
@Composable
fun PreviewWalkthroughScreen() {
//    WalkthroughScreen()
}