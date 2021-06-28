package com.jlp.unforgotchi.db

import androidx.lifecycle.LiveData

class ReminderListRepository(private val reminderListDao: ReminderListDao) {
    val readAllData: LiveData<List<ReminderList>> = reminderListDao.readAllData()

    suspend fun addReminderList(reminderList: ReminderList){
        reminderListDao.addReminderList(reminderList)
    }

}