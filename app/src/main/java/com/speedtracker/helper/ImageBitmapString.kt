package com.speedtracker.helper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.lang.Exception

object ImageBitmapString {

    fun BitMapToString(bitmap: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b: ByteArray = baos.toByteArray()
        val temp: String = Base64.encodeToString(b, Base64.DEFAULT)
        return if (temp == null) {
            null
        } else temp
    }

    fun StringToBitMap(encodedString: String?): Bitmap? {
        return try {
            val encodeByte: ByteArray = Base64.decode(encodedString, Base64.DEFAULT)
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
            if (bitmap == null) {
                null
            } else {
                bitmap
            }
        } catch (e: Exception) {
            e.message
            null
        }
    }
}