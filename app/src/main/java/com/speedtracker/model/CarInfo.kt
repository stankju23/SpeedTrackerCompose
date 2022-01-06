package com.speedtracker.model

import android.content.Context
import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import java.util.*
import kotlin.collections.ArrayList

@Entity
data class CarInfo(
    @PrimaryKey(autoGenerate = true) var id:Int?,
    @ColumnInfo(name = "carIdentifier") var carIdentifier:String,
    @ColumnInfo(name = "carBrand") var carBrand:String,
    @ColumnInfo(name = "carModel") var carModel:String,
    @ColumnInfo(name = "carManufacturedYear") var carManufacturedYear:String,
    @ColumnInfo(name = "carPhotoPath")var carPhotoPath:String? = null,
)

@Entity
data class TripData (
    @PrimaryKey()  var id: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "tripName") var tripName: String? = null,
    @ColumnInfo(name = "sumOfTripSpeed") var sumOfTripSpeed: Int = 0,
    @ColumnInfo(name = "countOfUpdates") var countOfUpdates: Int = 0,
    @ColumnInfo(name = "maxSpeed") var maxSpeed: Int = 0,
    @ColumnInfo(name = "distance") var distance: Float = 0F,
    @ColumnInfo(name = "tripStartDate") var tripStartDate: Date? = null,
    @ColumnInfo(name = "tripEndDate") var tripEndDate: Date? = null,
    @ColumnInfo(name = "locations") var locations: List<Location> = ArrayList()
)

@Entity
open class Location (
    @PrimaryKey(autoGenerate = true) var id:Int?,
    @ColumnInfo(name = "latitude") var latitude: Double = 0.0,
    @ColumnInfo(name = "longitude") var longitude: Double = 0.0,
    @ColumnInfo(name = "altitude") var altitude: Double = 0.0,
    @ColumnInfo(name = "time") var time: Long = 0,
)


@Dao
interface CarInfoDao {

    @Query("SELECT * from Carinfo")
    suspend fun getAllCarInfos(): List<CarInfo>

    @Insert(onConflict = IGNORE)
    suspend fun insertCarInfo(carInfo: CarInfo)

}

@Dao
interface TripDao {

    @Query("SELECT * from TripData")
    suspend fun getAllTripData(): List<TripData>

    @Insert(onConflict = IGNORE)
    suspend fun insertTripData(tripData: TripData)

    @Update
    suspend fun updateTripData(tripData: TripData)

}

@Database(entities = [CarInfo::class,TripData::class,Location::class], exportSchema = false, version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun carInfoDao():CarInfoDao
    abstract fun tripDao():TripDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context):AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_dabatase"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}