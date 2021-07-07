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

    @Query("SELECT * FROM reminder_list_elements_table WHERE list=0 ORDER BY id ASC")
    fun readAllElementsFromList1(): LiveData<List<ReminderListElement>>

    @Query("SELECT * FROM reminder_list_elements_table WHERE list=1 ORDER BY id ASC")
    fun readAllElementsFromList2(): LiveData<List<ReminderListElement>>

    @Query("SELECT * FROM reminder_list_elements_table WHERE list=2 ORDER BY id ASC")
    fun readAllElementsFromList3(): LiveData<List<ReminderListElement>>

    @Query("SELECT * FROM reminder_list_elements_table WHERE list=3 ORDER BY id ASC")
    fun readAllElementsFromList4(): LiveData<List<ReminderListElement>>

    @Query("SELECT * FROM reminder_list_elements_table WHERE list=4 ORDER BY id ASC")
    fun readAllElementsFromList5(): LiveData<List<ReminderListElement>>

    @Query("SELECT * FROM reminder_list_elements_table WHERE list=5 ORDER BY id ASC")
    fun readAllElementsFromList6(): LiveData<List<ReminderListElement>>

    @Query("SELECT * FROM reminder_list_elements_table WHERE list=6 ORDER BY id ASC")
    fun readAllElementsFromList7(): LiveData<List<ReminderListElement>>

    @Query("SELECT * FROM reminder_list_elements_table WHERE list=7 ORDER BY id ASC")
    fun readAllElementsFromList8(): LiveData<List<ReminderListElement>>

    @Query("SELECT * FROM reminder_list_elements_table WHERE list=8 ORDER BY id ASC")
    fun readAllElementsFromList9(): LiveData<List<ReminderListElement>>

    @Query("SELECT * FROM reminder_list_elements_table WHERE list=9 ORDER BY id ASC")
    fun readAllElementsFromList10(): LiveData<List<ReminderListElement>>

}