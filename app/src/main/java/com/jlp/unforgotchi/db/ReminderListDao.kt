package com.jlp.unforgotchi.db

import androidx.appcompat.widget.ResourceManagerInternal
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ReminderListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addReminderList(reminderList: ReminderList)

    @Query("SELECT * FROM reminder_list_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<ReminderList>>

    /*@Query("SELECT * FROM reminder_list WHERE reminder_list_name LIKE :name")
    fun findByName(name: String): ReminderList

    @Update
    fun updateTodo(vararg reminderLists: ReminderList)*/
}
