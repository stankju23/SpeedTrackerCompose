package com.speedtracker.model

import com.google.gson.annotations.SerializedName

class Car {

    @SerializedName("brand")
    lateinit var brand:String
    @SerializedName("models")
    lateinit var models:ArrayList<String>
}