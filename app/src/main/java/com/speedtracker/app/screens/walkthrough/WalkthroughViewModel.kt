package com.speedtracker.app.screens.walkthrough

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.speedtracker.R
import java.util.*
import kotlin.collections.ArrayList

class WalkthroughViewModel:ViewModel() {

    var brandList:MutableLiveData<List<String>> = MutableLiveData(listOf())
    var modelList:MutableLiveData<List<String>> = MutableLiveData(listOf("Choose Your Model"))

    var carBrand:MutableLiveData<String> = MutableLiveData("")
    var carModel:MutableLiveData<String> = MutableLiveData("")

    var carImageUri:MutableLiveData<Uri> = MutableLiveData()
    var manufacturedYear:MutableLiveData<Int> = MutableLiveData(0)

    var carBrandIndex:MutableLiveData<Int> = MutableLiveData(0)
    var carModelIndex:MutableLiveData<Int> = MutableLiveData(0)

    var showErrorDialog:MutableLiveData<Boolean> = MutableLiveData(false)
    var errors:ArrayList<Errors> = ArrayList()


    fun storeCarPreferences(){
        validateData()
        if (errors.size > 0) {
            showErrorDialog.value = true
        }
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