package com.speedtracker.app.screens.walkthrough

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.speedtracker.R
import com.speedtracker.model.AppDatabase
import com.speedtracker.model.CarInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class WalkthroughViewModel:ViewModel() {

    var brandList:MutableLiveData<List<String>> = MutableLiveData(listOf())
    var modelList:MutableLiveData<List<String>> = MutableLiveData(listOf("Choose Your Model"))

    var carBrand:MutableLiveData<String> = MutableLiveData("")
    var carModel:MutableLiveData<String> = MutableLiveData("")

    var carImage:MutableLiveData<String> = MutableLiveData()
    var manufacturedYear:MutableLiveData<Int> = MutableLiveData(0)

    var carBrandIndex:MutableLiveData<Int> = MutableLiveData(0)
    var carModelIndex:MutableLiveData<Int> = MutableLiveData(0)

    var showErrorDialog:MutableLiveData<Boolean> = MutableLiveData(false)
    var errors:ArrayList<Errors> = ArrayList()


    // return value for moving to app
    fun storeCarPreferences(context: Context,scope: CoroutineScope):Boolean{
        validateData()
        if (errors.size > 0) {
            showErrorDialog.value = true
            return false
        } else {
            if (errors.size == 0) {
                var carInfo = CarInfo(
                    id = null,
                    carIdentifier = UUID.randomUUID().toString(),
                    carBrand = carBrand.value!!,
                    carModel = carModel.value!!,
                    carManufacturedYear = manufacturedYear.value!!.toString(),
                    carPhoto = if (carImage.value != null) carImage.value!! else null
                )
//                Log.i("tag222", carInfo.carPhotoPath.toString())
                scope.launch {
                    AppDatabase.getDatabase(context = context).carInfoDao().insertCarInfo(carInfo = carInfo)
                }
                return true
            }
        }
        return false
    }

    private fun validateData() {
        errors.clear()
        if (carBrandIndex.value == 0 ) {
            errors.add(Errors.CAR_BRAND_MISSING)
            errors.add(Errors.CAR_MODEL_MISSING)
        } else {
            if (carModelIndex.value == 0) {
                errors.add(Errors.CAR_MODEL_MISSING)
            }
        }

        if (manufacturedYear.value == null || manufacturedYear.value == 0) {
            errors.add(Errors.CAR_YEAR_MISSING)
        } else {
            if (manufacturedYear.value!! < 1950 || manufacturedYear.value!! > Calendar.getInstance().get(Calendar.YEAR)) {
                errors.add(Errors.CAR_YEAR_NOT_CORRECT)
            }
        }
    }


    enum class Errors(val textId:Int) {
        CAR_BRAND_MISSING(R.string.no_car_brand),
        CAR_MODEL_MISSING(R.string.no_car_model),
        CAR_YEAR_MISSING(R.string.no_car_year),
        CAR_YEAR_NOT_CORRECT(R.string.car_year_not_correct)
    }
}