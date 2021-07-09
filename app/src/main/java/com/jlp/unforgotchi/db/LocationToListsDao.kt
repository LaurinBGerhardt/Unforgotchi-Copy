package com.jlp.unforgotchi.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LocationToListsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveLocation(location: Location)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveLists(vararg reminderListElements: ReminderListElement)

    @Transaction
    @Query("SELECT * FROM locations_table ORDER BY location_id ASC")
    fun readAll(): LiveData<List<LocationToLists>>

    @Transaction
    @Query("SELECT * FROM locations_table WHERE location_id = :id")
    suspend fun getByLocationId(id: Int): LocationToLists

    //I'm not quite sure whether the following ones will work:

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLocation(location: Location)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLists(vararg reminderListElements: ReminderListElement)

    @Delete
    suspend fun deleteLocation(location: Location)

    @Delete
    suspend fun deleteLists(vararg reminderListElements: ReminderListElement)

}
