package com.jlp.unforgotchi.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jlp.unforgotchi.db.ReminderList

@Dao
interface ReminderListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addReminderList(reminderList: ReminderList)

    @Update
    suspend fun updateReminderList(reminderList: ReminderList)

    @Query("SELECT * FROM reminder_list_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<ReminderList>>

    /*@Query("SELECT COUNT(*) FROM reminder_list_table")
    fun getCount(): LiveData<Int>*/

    @Delete
    suspend fun deleteReminderList(reminderList: ReminderList)

}
