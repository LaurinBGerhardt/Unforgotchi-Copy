package com.jlp.unforgotchi.db

import androidx.lifecycle.LiveData


class ReminderListElementRepository(private val reminderListElementDao: ReminderListElementDao) {

    val readAllElements: LiveData<List<ReminderListElement>> = reminderListElementDao.readAllElements()

    suspend fun addReminderListElement(reminderListElement: ReminderListElement){
        reminderListElementDao.addReminderListElement(reminderListElement)
    }

    suspend fun updateReminderListElement(reminderListElement: ReminderListElement){
        reminderListElementDao.updateReminderListElement(reminderListElement)
    }

    suspend fun deleteReminderListElement(reminderListElement: ReminderListElement){
        reminderListElementDao.deleteReminderListElement(reminderListElement)
    }
}