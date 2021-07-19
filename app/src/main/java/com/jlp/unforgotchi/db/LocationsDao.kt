package com.jlp.unforgotchi.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LocationsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLocation(location: Location)

    @Query("SELECT * FROM locations_table ORDER BY location_id ASC")
    fun readAllLocations(): LiveData<List<Location>>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLocation(location: Location)

    @Query("SELECT * FROM locations_table WHERE location_id = :id")
    suspend fun getLocationById(id : Int) : Location

    @Delete
    suspend fun deleteLocation(location: Location)

    @Query("SELECT wifi_name FROM locations_table ORDER BY location_id ASC")
    fun getAllWifis() : LiveData<List<String?>>

    //This SHOULD work, but somehow room changes the value always to 0 for no reason at all
    //This function is deprecated so to speak. It's still here because in the future we
    //may fix it
    //@Query("UPDATE locations_table SET is_latest = CASE WHEN location_id = :latest_location_id THEN 1 ELSE 0 END")
    //fun setLatestLocation(latest_location_id: Int)
}