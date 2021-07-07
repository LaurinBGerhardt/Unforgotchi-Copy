package com.jlp.unforgotchi.db

import androidx.lifecycle.LiveData


class ReminderListElementRepository(private val reminderListElementDao: ReminderListElementDao) {

    val readAllElements: LiveData<List<ReminderListElement>> = reminderListElementDao.readAllElements()

    val readAllElementsFromList1: LiveData<List<ReminderListElement>> = reminderListElementDao.readAllElementsFromList1()

    val readAllElementsFromList2: LiveData<List<ReminderListElement>> = reminderListElementDao.readAllElementsFromList2()

    val readAllElementsFromList3: LiveData<List<ReminderListElement>> = reminderListElementDao.readAllElementsFromList3()

    val readAllElementsFromList4: LiveData<List<ReminderListElement>> = reminderListElementDao.readAllElementsFromList4()

    val readAllElementsFromList5: LiveData<List<ReminderListElement>> = reminderListElementDao.readAllElementsFromList5()

    val readAllElementsFromList6: LiveData<List<ReminderListElement>> = reminderListElementDao.readAllElementsFromList6()

    val readAllElementsFromList7: LiveData<List<ReminderListElement>> = reminderListElementDao.readAllElementsFromList7()

    val readAllElementsFromList8: LiveData<List<ReminderListElement>> = reminderListElementDao.readAllElementsFromList8()

    val readAllElementsFromList9: LiveData<List<ReminderListElement>> = reminderListElementDao.readAllElementsFromList9()

    val readAllElementsFromList10: LiveData<List<ReminderListElement>> = reminderListElementDao.readAllElementsFromList10()


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