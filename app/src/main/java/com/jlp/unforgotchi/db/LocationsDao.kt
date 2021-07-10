package com.jlp.unforgotchi.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LocationsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addLocation(location: Location)

    @Query("SELECT * FROM locations_table ORDER BY location_id ASC")
    fun readAllLocations(): LiveData<List<Location>>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLocation(location: Location)

    @Delete
    suspend fun deleteLocation(location: Location)

    @Query("SELECT wifiName FROM locations_table ORDER BY location_id ASC")
    fun getAllWifis() : LiveData<List<String?>>
}