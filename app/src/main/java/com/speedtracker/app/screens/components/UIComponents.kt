package com.speedtracker.app.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontLoader
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoadingComponent(modifier: Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .width(15.dp)
                .height(15.dp),
            color = Color.White,
            strokeWidth = 1.5.dp
        )

        Column(
            modifier = Modifier
                .padding(start = 6.dp),
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = "Searching for GPS location...",
                color = Color.White,
                fontSize = 12.sp
            )
        }

    }
}



@Composable
fun AutoSizeText(
    text: String,
    minTextSizeSp: Float,
    maxTextSizeSp: Float,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    textAlign: TextAlign? = null,
    style: TextStyle = LocalTextStyle.current,
    contentAlignment: Alignment = Alignment.TopStart,
) {
    check(minTextSizeSp > 0) { "Min text size should above zero" }
    check(minTextSizeSp < maxTextSizeSp) { "Min text size should be smaller then max text size" }
    BoxWithConstraints(modifier, contentAlignment = contentAlignment) {
        val textString = text
        val currentStyle = style.copy(
            color = color,
            fontStyle = fontStyle ?: style.fontStyle,
            fontSize = maxTextSizeSp.sp,
            fontWeight = fontWeight ?: style.fontWeight,
            fontFamily = fontFamily ?: style.fontFamily,
            textAlign = textAlign,
        )
        val fontChecker = createFontChecker(currentStyle, textString)
        val fontSize = remember(textString) {
            fontChecker.findMaxFittingTextSize(minTextSizeSp, maxTextSizeSp)
        }

        Text(
            text = text,
            style = currentStyle + TextStyle(fontSize = fontSize),
            color = color,
            textAlign = textAlign
        )
    }
}

@Composable
private fun BoxWithConstraintsScope.createFontChecker(currentStyle: TextStyle, text: String): FontChecker {
    val density = LocalDensity.current
    return FontChecker(
        density = density,
        resourceLoader = LocalFontLoader.current,
        maxWidthPx = with (density) { maxWidth.toPx() },
        maxHeightPx = with (density) { maxHeight.toPx() },
        currentStyle = currentStyle,
        text = text
    )
}

private class FontChecker(
    private val density: Density,
    private val resourceLoader: Font.ResourceLoader,
    private val maxWidthPx: Float,
    private val maxHeightPx: Float,
    private val currentStyle: TextStyle,
    private val text: String
) {

    fun isFit(fontSizeSp: Float): Boolean {
        val height = Paragraph(
            text = text,
            style = currentStyle + TextStyle(fontSize = fontSizeSp.sp),
            width = maxWidthPx,
            density = density,
            resourceLoader = resourceLoader,
        ).height
        return height <= maxHeightPx
    }

    fun findMaxFittingTextSize(
        minTextSizeSp: Float,
        maxTextSizeSp: Float
    ) = if (!isFit(minTextSizeSp)) {
        minTextSizeSp.sp
    } else if (isFit(maxTextSizeSp)) {
        maxTextSizeSp.sp
    } else {
        var fit = minTextSizeSp
        var unfit = maxTextSizeSp
        while (unfit - fit > 1) {
            val current = fit + (unfit - fit) / 2
            if (isFit(current)) {
                fit = current
            } else {
                unfit = current
            }
        }
        fit.sp
    }
}