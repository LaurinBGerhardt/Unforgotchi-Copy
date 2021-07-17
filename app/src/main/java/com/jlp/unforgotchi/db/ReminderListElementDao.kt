package com.jlp.unforgotchi.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ReminderListElementDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addReminderListElement(reminderListElement: ReminderListElement)

    @Query("SELECT * FROM reminder_list_elements_table")
    fun readAllElements(): LiveData<List<ReminderListElement>>

    @Update
    suspend fun updateReminderListElement(reminderListElement: ReminderListElement)

    @Delete
    suspend fun deleteReminderListElement(reminderListElement: ReminderListElement)
}