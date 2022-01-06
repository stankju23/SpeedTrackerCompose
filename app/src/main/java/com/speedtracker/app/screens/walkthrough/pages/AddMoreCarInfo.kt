package com.speedtracker.app.screens.walkthrough.pages

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.speedtracker.R
import com.speedtracker.ui.theme.Typography
import java.util.*

@Composable
fun AddMoreCarInfo(manufacturedYear:MutableLiveData<Int>,imageUriLiveData:MutableLiveData<Uri>,context:Context) {

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val launcher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
        imageUriLiveData.value = uri
    }
    val bitmap =  remember {
        mutableStateOf<Bitmap?>(null)
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "You can add photo by tapping \n on the image below",
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
                .border(
                    width = 2.dp,
                    color = Color.White,
                    shape = CircleShape
                ),
            onClick = {
                launcher.launch("image/*")
            })
        {
            imageUri?.let {
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap.value = MediaStore.Images
                        .Media.getBitmap(context.contentResolver,it)

                } else {
                    val source = ImageDecoder
                        .createSource(context.contentResolver,it)
                    bitmap.value = ImageDecoder.decodeBitmap(source)
                }

                bitmap.value?.let {  btm ->
                    Image(bitmap = btm.asImageBitmap(),
                        contentDescription =null,
                        modifier = Modifier.size(200.dp) .clip(CircleShape))
                }
            }
            if (imageUri == null) {
                Icon(
                    Icons.Rounded.PhotoCamera,
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp),
                    contentDescription = "Photo image",
                    tint = Color.White)
            }

        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "You can type manufacture \n year into field below",
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = Color.White,
        )

        Spacer(modifier = Modifier.weight(1f))

        var text by remember { mutableStateOf("") }
        var isError by rememberSaveable { mutableStateOf(false) }

        fun validate(text: String) {
            if (text.length == 4) {
                if (text.toInt() < 1950 || text.toInt() > Calendar.getInstance().get(Calendar.YEAR)) {
                    isError = true
                } else {
                    isError = false
                }
            } else {
                if (isError == true) {
                    isError = false
                }
            }

        }

        Spacer(modifier = Modifier.weight(1f))

        Column {
        TextField(
            value = text,
            textStyle = Typography.labelSmall,
            label = { Text(text = "Here you can type year", color = Color.White)},
            onValueChange = {
                text = it
                manufacturedYear.value = if(it.isEmpty()) 0 else it.toInt()
                validate(text)
            },
            trailingIcon = {
                if (isError)
                    Icon(Icons.Filled.Info,"error", tint = androidx.compose.material.MaterialTheme.colors.error)
            },
            singleLine = true,
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions { validate(text) }
        )
            if (isError) {
                Text(
                    text = "Type correct year",
                    color = MaterialTheme.colorScheme.error,
                    style = androidx.compose.material.MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview
@Composable
fun PreviewAddMoreCarInfo() {
    AddMoreCarInfo(MutableLiveData(0),MutableLiveData(),LocalContext.current,)
}