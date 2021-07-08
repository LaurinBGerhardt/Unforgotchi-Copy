package com.jlp.unforgotchi.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LocationToListsDao {
    @Insert
    suspend fun saveLocation(location: Location)

    @Insert
    suspend fun saveLists(vararg reminderListElements: ReminderListElement)

    @Insert
    suspend fun saveList(reminderListElement: ReminderListElement)

    @Transaction
    @Query("SELECT * FROM locations_table ORDER BY id ASC")
    fun readAll(): LiveData<List<LocationToLists>>

    @Transaction
    @Query("SELECT * FROM locations_table WHERE id = :id")
    suspend fun getByLocationId(id: Int): LocationToLists

    //I'm not quite sure whether the following ones will work:

    @Update
    suspend fun updateLocation(location: Location)

    @Update
    suspend fun updateLists(vararg reminderListElements: ReminderListElement)

    @Update fun updateList(reminderListElement: ReminderListElement)

    @Delete
    suspend fun deleteLocation(location: Location)

    @Delete
    suspend fun deleteLists(vararg reminderListElements: ReminderListElement)

    @Delete
    fun deleteList(reminderListElement: ReminderListElement)
}
