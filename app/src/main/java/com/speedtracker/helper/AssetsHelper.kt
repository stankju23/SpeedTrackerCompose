package com.speedtracker.helper

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.speedtracker.model.Car
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class AssetsHelper {

    fun loadJSONFromAsset(contextVar: Context): String? {
        var json: String? = null
        json = try {
            val inputStream: InputStream = contextVar.assets.open("cars.json")
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    fun parseCarsBrands(context: Context) :List<Car>{
        var carsString = loadJSONFromAsset(context)
        val gson = Gson()
        val carsListType = object : TypeToken<List<Car>>() {}.type
        val carsList = gson.fromJson<List<Car>>(carsString, carsListType)
        Collections.sort(carsList, object: Comparator<Car> {
            override fun compare(o1: Car?, o2: Car?): Int {
                return o1!!.brand.compareTo(o2!!.brand,true)
            }

        })
        return carsList
    }

    fun sortArrayAlphabetically(list:ArrayList<String>) {
        Collections.sort(list , object : Comparator<String> {
            override fun compare(o1: String?, o2: String?): Int {
                return o1!!.compareTo(o2!!,true)
            }
        })
    }
}