package com.speedtracker.app.screens.walkthrough.pages

import android.R.attr.data
import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.speedtracker.R
import com.speedtracker.ui.theme.Typography
import java.io.File
import java.util.*


@Composable
fun AddMoreCarInfo(manufacturedYear:MutableLiveData<Int>, imageLiveData:MutableLiveData<String>, context:Context) {

//    val bitmap =  remember {
//        mutableStateOf<Bitmap?>(null)
//    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.walkthroug_add_photo_title),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.weight(1f))

        CarPhotoImage(context = context, imageLiveData = imageLiveData)

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.walkthrough_manufacture_year_title),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = Color.White,
        )


        Spacer(modifier = Modifier.weight(1f))
        TypeYearTextField(manufacturedYear = manufacturedYear, initialText = "")
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun TypeYearTextField(manufacturedYear: MutableLiveData<Int>,initialText:String) {
    var text by remember { mutableStateOf(initialText) }
    var isError by rememberSaveable { mutableStateOf(false) }

    fun validate(text: String) {
        if (text.length == 4) {
            try {
                if (text.toInt() < 1950 || text.toInt() > Calendar.getInstance().get(Calendar.YEAR)) {
                    isError = true
                } else {
                    isError = false
                }
            } catch (e:Exception) {
                isError = true
            }

        } else {
            if (isError == true) {
                isError = false
            }
        }

    }
    Column {
        TextField(
            value = text,
            textStyle = Typography.labelSmall,
            label = { Text(text = stringResource(R.string.walkthrough_type_year_hint), color = Color.White)},
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
                text = stringResource(R.string.walkthrough_type_year_error_message),
                color = MaterialTheme.colorScheme.error,
                style = androidx.compose.material.MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
fun CarPhotoImage(context: Context, imageLiveData: MutableLiveData<String>) {


    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }


    val launcher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.OpenDocument()) { uri: Uri? ->
//        val file = File(uri!!.path) //create path from uri
////        val split = file.path.split(":").toTypedArray() //split the path.
//        // assign it to a string(your choice).
//        var filePath = file.path

        imageLiveData.value = uri!!.path
        imageUri = uri
    }


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
            launcher.launch(arrayOf("image/*"))
        })
    {
        imageUri?.let {

            val contentResolver = LocalContext.current.contentResolver

            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
            // Check for the freshest data.
            contentResolver.takePersistableUriPermission(imageUri!!, takeFlags)

            var image: Bitmap
            if (Build.VERSION.SDK_INT < 28) {
                image = MediaStore.Images.Media.getBitmap(
                    context.contentResolver,it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver,it)
                image =ImageDecoder.decodeBitmap(source)
            }

            Image(
                bitmap = image.asImageBitmap(),
                contentDescription =null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape))
            imageLiveData.value = imageUri.toString()
//                LaunchedEffect(key1 = "") {
//                    imageLiveData.value = ImageBitmapString.BitMapToString(image)
//                }

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
}

@Preview
@Composable
fun PreviewAddMoreCarInfo() {
    AddMoreCarInfo(MutableLiveData(0), MutableLiveData(), LocalContext.current)
}