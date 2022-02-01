@file:OptIn(ExperimentalMaterial3Api::class)

package com.speedtracker.app.screens.settings

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.speedtracker.app.screens.mainscreen.statistics.StatisticsViewModel
import com.speedtracker.helper.GenerallData
import com.speedtracker.ui.theme.MainGradientEndColor
import com.speedtracker.ui.theme.MainGradientStartColor
import com.speedtracker.ui.theme.Nunito
import com.speedtracker.ui.theme.Pink40

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsScreen(paddingValues: PaddingValues,context: Context, settingsViewModel: SettingsViewModel,statisticsViewModel:StatisticsViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings", color = Color.White, fontFamily = Nunito) },
                backgroundColor = MainGradientStartColor
//                navigationIcon = {
//                    IconButton(onClick = {(context as Activity).onBackPressed()}) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "BackArrow", tint = Color.White)
//                    }
//                }
            )
        }
    ) {

        LazyColumn(Modifier.fillMaxSize().padding(bottom = paddingValues.calculateBottomPadding())) {

            item {
                SettingSection(title = "General") {
                    SettingsItem(
                        modifier = Modifier,
                        title = "Metric system",
                        subtitle = "You can choose between metric or imperial unit system. Metric is set by default.",
                        rightPart = {
                            val checkedState = remember { mutableStateOf(GenerallData.isMetric.value!!) }
                            Switch(modifier = Modifier.padding(start = 20.dp,end = 12.dp),
                                checked = checkedState.value,
                                onCheckedChange = {
                                    checkedState.value = it
                                    GenerallData.isMetric.value = it
                                    settingsViewModel.updateIsMetricSetting(it, statisticsViewModel = statisticsViewModel, context = context)
                                })
                        })
                    Separator()
                    SettingsItem(modifier = Modifier.clickable { }, title = "Reset App", subtitle = "Click to remove all stored data.", rightPart = null)
                }

                SettingSection(title = "Licences") {
                    SettingsItem(modifier = Modifier, title = "Freepik", subtitle = "Flaticon")
                    Separator()
                    SettingsItem(modifier = Modifier, title = "Those Icons", subtitle = "Flaticon")
                    Separator()
                    SettingsItem(modifier = Modifier, title = "Vitaly Gorbachev", subtitle = "Flaticon")
                    Separator()
                    SettingsItem(modifier = Modifier, title = "fjstudio", subtitle = "Flaticon")
                    Separator()
                    SettingsItem(modifier = Modifier, title = "Kiranshastry", subtitle = "Flaticon")
                    Separator()
                    SettingsItem(modifier = Modifier, title = "Freepik", subtitle = "Flaticon")
                    Separator()
                    SettingsItem(modifier = Modifier, title = "Those Icons", subtitle = "Flaticon")
                    Separator()
                    SettingsItem(modifier = Modifier, title = "Vitaly Gorbachev", subtitle = "Flaticon")
                    Separator()
                    SettingsItem(modifier = Modifier, title = "fjstudio", subtitle = "Flaticon")
                    Separator()
                    SettingsItem(modifier = Modifier, title = "Kiranshastry", subtitle = "Flaticon")

                }

                SettingSection(title = "About") {
                    Text(modifier = Modifier.padding(start = 16.dp, end = 16.dp), text = "We are team of two peaople who make this project as free time project and we are really car fans and want to track our trips and see the correct speed by gps.",
                        fontSize = 12.sp,
                        fontFamily = Nunito,
                        color = Color.Gray)
                    SettingsItem(modifier = Modifier, title = "App version", subtitle = "v1.0.0")
                }
            }
        }
    }
}

@Composable
fun Separator() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(0.7.dp)
        .background(MainGradientEndColor.copy(alpha = 0.3f)))
}

@Composable
fun SettingsItem(modifier: Modifier,title:String, subtitle:String, rightPart: @Composable (() -> Unit)? = null) {
    Row(modifier = modifier
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier
            .padding(start = 16.dp, top = 12.dp, bottom = 14.dp)
            .weight(1f)) {
            Text(text = title,
                fontSize = 16.sp,
                fontFamily = Nunito,
                color = Color.Black,
                fontWeight = FontWeight.Normal)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle,
                fontSize = 12.sp,
                fontFamily = Nunito,
                color = Color.Gray)
        }
        if (rightPart != null) {
            Box(contentAlignment = Alignment.CenterEnd) {
                rightPart()
            }
        }

    }
}

@Composable
fun SettingSection(title:String,SettingsItems: @Composable (() -> Unit)) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = title,
            color = Pink40,
            fontSize = 15.sp,
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp))
        SettingsItems()
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        color = Pink40,
        fontSize = 15.sp,
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth())
}


@Preview
@Composable
fun PreviewSettingsItem() {
    val checkedState = remember { mutableStateOf(true) }
    SettingsItem(modifier = Modifier, title = "", subtitle = "", rightPart = {
        Switch(modifier = Modifier.padding(start = 20.dp,end = 12.dp),
            checked = checkedState.value,
            onCheckedChange = {
                checkedState.value = it
            })
    })
}

//@Preview
//@Composable
//fun PreviewSettingsScreen() {
//    SettingsScreen(context = LocalContext.current, SettingsViewModel())
//}