package com.speedtracker.model

import android.content.Context
import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE

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
data class TripInfo (
    @PrimaryKey()  var tripId: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "tripName") var tripName: String? = null,
    @ColumnInfo(name = "sumOfTripSpeed") var sumOfTripSpeed: Int = 0,
    @ColumnInfo(name = "countOfUpdates") var countOfUpdates: Int = 0,
    @ColumnInfo(name = "maxSpeed") var maxSpeed: Int = 0,
    @ColumnInfo(name = "distance") var distance: Double = 0.0,
    @ColumnInfo(name = "tripStartDate") var tripStartDate: Long? = null,
    @ColumnInfo(name = "tripEndDate") var tripEndDate: Long? = null
)

@Entity
data class Location (
    @PrimaryKey(autoGenerate = true) var locationId:Int?,
    @ColumnInfo(name = "tripIdentifier") val tripIdentifier: Long,
    @ColumnInfo(name = "latitude") var latitude: Double = 0.0,
    @ColumnInfo(name = "longitude") var longitude: Double = 0.0,
    @ColumnInfo(name = "altitude") var altitude: Double = 0.0,
    @ColumnInfo(name = "time") var time: Long = 0,
)

data class TripData (
    @Embedded val tripInfo: TripInfo,
    @Relation(
        parentColumn = "tripId",
        entityColumn = "tripIdentifier"
    )
    val locations: List<Location>
)


@Dao
interface CarInfoDao {

    @Query("DELETE  FROM Carinfo")
    fun deleteCarInfos()

    @Query("SELECT * from Carinfo")
    suspend fun getAllCarInfos(): List<CarInfo>

    @Insert(onConflict = IGNORE)
    suspend fun insertCarInfo(carInfo: CarInfo)

}

@Dao
interface TripDao {

    @Transaction
    @Query("SELECT * FROM TripInfo")
    suspend fun getAllTripData(): List<TripData>

    @Insert(onConflict = IGNORE)
    suspend fun insertTripInfo(tripInfo: TripInfo)

    @Insert(onConflict = IGNORE)
    suspend fun addLocation(location: Location)

    @Update
    suspend fun updateTrip(tripInfo: TripInfo)

    @Transaction
    @Query("SELECT * FROM TripInfo WHERE tripId = :id")
    suspend fun getTripDataById(id: Long): TripData

}

@Database(entities = [CarInfo::class,TripInfo::class,Location::class], exportSchema = false, version = 1)
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