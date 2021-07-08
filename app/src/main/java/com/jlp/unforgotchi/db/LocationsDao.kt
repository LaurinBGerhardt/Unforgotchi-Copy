package com.jlp.unforgotchi.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LocationsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addLocation(location: Location)

    @Query("SELECT * FROM locations_table ORDER BY id ASC")
    fun readAllLocations(): LiveData<List<Location>>

    @Update
    suspend fun updateLocation(location: Location)

    @Delete
    suspend fun deleteLocation(location: Location)

}