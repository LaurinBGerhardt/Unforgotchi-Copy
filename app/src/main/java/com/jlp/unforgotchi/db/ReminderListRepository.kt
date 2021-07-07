package com.jlp.unforgotchi.db

import androidx.lifecycle.LiveData
import com.jlp.unforgotchi.db.ReminderList
import com.jlp.unforgotchi.db.ReminderListDao

class ReminderListRepository(private val reminderListDao: ReminderListDao) {

    val readAllData: LiveData<List<ReminderList>> = reminderListDao.readAllData()

    //val getCount: LiveData<Int> = reminderListDao.getCount()

    suspend fun addReminderList(reminderList: ReminderList){
        reminderListDao.addReminderList(reminderList)
    }

    suspend fun updateReminderList(reminderList: ReminderList){
        reminderListDao.updateReminderList(reminderList)
    }

    suspend fun deleteReminderList(reminderList: ReminderList){
        reminderListDao.deleteReminderList(reminderList)
    }
}