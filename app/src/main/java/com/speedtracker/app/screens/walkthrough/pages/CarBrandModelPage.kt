package com.speedtracker.app.screens.walkthrough.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.speedtracker.R
import com.speedtracker.app.screens.walkthrough.WalkthroughViewModel
import com.speedtracker.helper.AssetsHelper
import com.speedtracker.model.Car
import com.speedtracker.ui.theme.MainGradientStartColor


@Composable
fun CarBrandModelPage(carList:List<Car>,walkthroughViewModel: WalkthroughViewModel) {
    Column(modifier = Modifier
        .fillMaxHeight()
        .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.ic_car_splash),
            contentDescription = "Car icon")
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Choose your car brand \n and model",
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = Color.White)
        Spacer(modifier = Modifier.weight(1f))

        var brandStringList = carList.map { car -> car.brand }
        AssetsHelper.sortArrayAlphabetically(brandStringList as ArrayList<String>)
        brandStringList.add(0,"Choose your brand")

        walkthroughViewModel.brandList.value = brandStringList

        Dropdown(listOfItems = walkthroughViewModel.brandList.observeAsState().value!!,
            itemClick = {
            if (it == -1) {
                walkthroughViewModel.modelList.value = listOf("Choose your model")
                walkthroughViewModel.carModelIndex.value = 0
            } else {
                var models = carList.get(it).models
                models.add(0,"Choose your model")
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
    }
}



@Composable
fun Dropdown(listOfItems:List<String>, itemClick:(Int) -> (Unit),selectedItem:MutableLiveData<Int>,selectedValue:MutableLiveData<String>) {
    var expanded by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    var dropDownWidth = screenWidth - 130.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopStart)
            .height(50.dp)
            .padding(start = 65.dp, end = 65.dp)
            .border(2.dp, color = if(listOfItems.size == 1) Color.Gray else Color.White, shape = RoundedCornerShape(10.dp)),

        ) {
        Row(modifier = Modifier
            .fillMaxSize()
            .clickable(
                onClick = {
                    if (listOfItems.size > 1) {
                        expanded = true
                    }
                })
            .background(Color.Transparent),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            Text(
                text = listOfItems[selectedItem.value!!],
                textAlign = TextAlign.Center,
                color = if(listOfItems.size == 1) Color.Gray else Color.White

            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(dropDownWidth)
                .border(2.dp, color = Color.White, shape = RoundedCornerShape(10.dp))
                .background(MainGradientStartColor)
        ) {
            listOfItems.forEachIndexed { index, s ->
                DropdownMenuItem(
                    onClick = {
                        selectedValue.value = s
                        selectedItem.value = index
                        expanded = false
                        itemClick(index-1)
                }) {
                    Text(text = s, color = Color.White)
                }
            }
        }
    }
}




@Preview
@Composable
fun PreviewCarBrandModelPage() {
    CarBrandModelPage(listOf(), WalkthroughViewModel())
}