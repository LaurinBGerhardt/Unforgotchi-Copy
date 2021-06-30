package com.jlp.unforgotchi.db

import androidx.appcompat.widget.ResourceManagerInternal
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ReminderListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addReminderList(reminderList: ReminderList)

    @Update
    suspend fun updateReminderList(reminderList: ReminderList)

    @Query("SELECT * FROM reminder_list_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<ReminderList>>

    @Delete
    suspend fun deleteReminderList(reminderList: ReminderList)
}
