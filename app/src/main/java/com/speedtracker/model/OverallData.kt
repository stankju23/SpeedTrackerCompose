package com.speedtracker.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class OverallData (
    var sumOfSpeeds: Float = 0.0f,
    var countOfUpdates: Int = 0,
    var maxSpeed:Float = 0f,
    var sumOfDistancesInM:Double = 0.0
) :Parcelable
{
    constructor(parcel: Parcel) : this(
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(sumOfSpeeds)
        parcel.writeInt(countOfUpdates)
        parcel.writeFloat(maxSpeed)
        parcel.writeDouble(sumOfDistancesInM)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OverallData> {
        override fun createFromParcel(parcel: Parcel): OverallData {
            return OverallData(parcel)
        }

        override fun newArray(size: Int): Array<OverallData?> {
            return arrayOfNulls(size)
        }
    }


}