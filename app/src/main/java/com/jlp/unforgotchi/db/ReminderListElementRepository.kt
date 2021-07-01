package com.jlp.unforgotchi.db

import androidx.lifecycle.LiveData


class ReminderListElementRepository(private val reminderListElementDao: ReminderListElementDao) {

    val readAllElements: LiveData<List<ReminderListElement>> = reminderListElementDao.readAllElements()

    suspend fun addReminderListElement(reminderListElement: ReminderListElement){
        reminderListElementDao.addReminderListElement(reminderListElement)
    }
}