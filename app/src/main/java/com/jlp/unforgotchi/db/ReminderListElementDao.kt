package com.jlp.unforgotchi.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReminderListElementDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addReminderListElement(reminderListElement: ReminderListElement)

    @Query("SELECT * FROM reminder_list_elements_table ORDER BY id ASC")
    fun readAllElements(): LiveData<List<ReminderListElement>>

}