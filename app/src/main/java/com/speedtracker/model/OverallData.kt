package com.speedtracker.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class OverallData (
    var sumOfSpeeds: Int = 0,
    var countOfUpdates: Int = 0,
    var maxSpeed:Int = 0,
    var sumOfDistancesInM:Double = 0.0
) :Parcelable
{
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(sumOfSpeeds)
        parcel.writeInt(countOfUpdates)
        parcel.writeInt(maxSpeed)
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