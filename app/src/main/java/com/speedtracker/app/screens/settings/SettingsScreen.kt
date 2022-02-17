@file:OptIn(ExperimentalMaterial3Api::class)

package com.speedtracker.app.screens.settings

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.speedtracker.R
import com.speedtracker.app.screens.mainscreen.statistics.StatisticsViewModel
import com.speedtracker.helper.GenerallData
import com.speedtracker.ui.theme.MainGradientEndColor
import com.speedtracker.ui.theme.MainGradientStartColor
import com.speedtracker.ui.theme.Nunito
import com.speedtracker.ui.theme.Pink40


var showResetAppDialog = MutableLiveData(false)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsScreen(paddingValues: PaddingValues,context: Context, settingsViewModel: SettingsViewModel,statisticsViewModel:StatisticsViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.settings_screen_title), color = Color.White, fontFamily = Nunito) },
                backgroundColor = MainGradientStartColor
//                navigationIcon = {
//                    IconButton(onClick = {(context as Activity).onBackPressed()}) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "BackArrow", tint = Color.White)
//                    }
//                }
            )
        }
    ) {

        ResetAppDialog(context = context, settingsViewModel = settingsViewModel)
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())) {

            item {
                SettingSection(title = stringResource(R.string.general_settings_title)) {
                    SettingsItem(
                        modifier = Modifier,
                        title = stringResource(R.string.metric_system_setting_title),
                        subtitle = stringResource(R.string.metric_system_setting_subtitle),
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
                    SettingsItem(modifier = Modifier.clickable {
                        showResetAppDialog.value = true
                    }, title = stringResource(R.string.reset_app_setting_title), subtitle = stringResource(R.string.reset_app_setting_subtitle), rightPart = null)
                }

                SettingSection(title = stringResource(R.string.licences_setting_title)) {
                    SettingsItem(modifier = Modifier.clickable { settingsViewModel.showWebPage(url = "https://www.freepik.com", context = context) }, title = "Freepik", subtitle = "Flaticon")
                    Separator()
                    SettingsItem(modifier = Modifier.clickable { settingsViewModel.showWebPage(url = "https://www.flaticon.com/authors/those-icons", context = context) }, title = "Those Icons", subtitle = "Flaticon")
                    Separator()
                    SettingsItem(modifier = Modifier.clickable { settingsViewModel.showWebPage(url = "https://www.flaticon.com/authors/vitaly-gorbachev", context = context) }, title = "Vitaly Gorbachev", subtitle = "Flaticon")
                    Separator()
                    SettingsItem(modifier = Modifier.clickable { settingsViewModel.showWebPage(url = "https://www.flaticon.com/authors/fjstudio", context = context) }, title = "fjstudio", subtitle = "Flaticon")
                    Separator()
                    SettingsItem(modifier = Modifier.clickable { settingsViewModel.showWebPage(url = "https://www.flaticon.com/authors/kiranshastry", context = context) }, title = "Kiranshastry", subtitle = "Flaticon")
                }

                SettingSection(title = stringResource(R.string.about_setting_title)) {
                    Text(modifier = Modifier.padding(start = 16.dp, end = 16.dp), text = stringResource(
                                            R.string.about_setting_subtitle),
                        fontSize = 12.sp,
                        fontFamily = Nunito,
                        color = Color.Gray)
                    SettingsItem(modifier = Modifier, title = stringResource(R.string.app_version_title), subtitle = stringResource(
                                            R.string.app_version_value)
                                        )
                }
            }
        }
    }
}

@Composable
fun ResetAppDialog(context:Context,settingsViewModel: SettingsViewModel) {
    
    val confirmButtonColor = ButtonDefaults.buttonColors(
        containerColor = Color.Transparent,
        contentColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
    )
    val dismissButtonColor = ButtonDefaults.buttonColors(
        containerColor = Color.Transparent,
        contentColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
    )
    if (showResetAppDialog.observeAsState().value!!) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                showResetAppDialog.value = false
            },
            title = {
                androidx.compose.material3.Text( 
                    text = stringResource(R.string.are_you_sure_dialog_title),
                    color = MainGradientStartColor,
                    fontSize = 26.sp
                )
            },
            text = {
                Column(modifier = Modifier.height(75.dp)) {
                    Text(text = stringResource(R.string.reset_app_dialog_subtitle),
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    color = Color.Gray)
                }
            },
            confirmButton = {
                Button(
                    colors = confirmButtonColor,
                    onClick = {
                        // Change the state to close the dialog
                        showResetAppDialog.value = false
                        settingsViewModel.resetApp(context = context)
                    },
                ) {
                    androidx.compose.material3.Text(stringResource(R.string.confirm_dialog_btn), color = Color.Black, fontSize = 16.sp)
                }
            },
            dismissButton = {
                Button(
                    colors = dismissButtonColor,
                    onClick = {
                        // Change the state to close the dialog
                        showResetAppDialog.value = false
                    },
                ) {
                    androidx.compose.material3.Text(text = stringResource(R.string.dismiss_dialog_btn), color = Color.Black, fontSize = 16.sp)
                }
            }
        )
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