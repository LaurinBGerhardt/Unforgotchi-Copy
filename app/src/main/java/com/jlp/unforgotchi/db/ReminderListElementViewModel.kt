package com.jlp.unforgotchi.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderListElementViewModel(application: Application): AndroidViewModel(application) {

    val readAllElement: LiveData<List<ReminderListElement>>

    val readAllElementsFromList1: LiveData<List<ReminderListElement>>
    val readAllElementsFromList2: LiveData<List<ReminderListElement>>
    val readAllElementsFromList3: LiveData<List<ReminderListElement>>
    val readAllElementsFromList4: LiveData<List<ReminderListElement>>
    val readAllElementsFromList5: LiveData<List<ReminderListElement>>
    val readAllElementsFromList6: LiveData<List<ReminderListElement>>
    val readAllElementsFromList7: LiveData<List<ReminderListElement>>
    val readAllElementsFromList8: LiveData<List<ReminderListElement>>
    val readAllElementsFromList9: LiveData<List<ReminderListElement>>
    val readAllElementsFromList10: LiveData<List<ReminderListElement>>




    private val repository: ReminderListElementRepository

    init {
        val reminderListElementDao = ReminderListElementDatabase.getDatabase(application).reminderListElementDao()
        repository = ReminderListElementRepository(reminderListElementDao)

        readAllElement = repository.readAllElements
        readAllElementsFromList1 = repository.readAllElementsFromList1
        readAllElementsFromList2 = repository.readAllElementsFromList2
        readAllElementsFromList3 = repository.readAllElementsFromList3
        readAllElementsFromList4 = repository.readAllElementsFromList4
        readAllElementsFromList5 = repository.readAllElementsFromList5
        readAllElementsFromList6 = repository.readAllElementsFromList6
        readAllElementsFromList7 = repository.readAllElementsFromList7
        readAllElementsFromList8 = repository.readAllElementsFromList8
        readAllElementsFromList9 = repository.readAllElementsFromList9
        readAllElementsFromList10 = repository.readAllElementsFromList10

    }

    fun addReminderListElement(reminderListElement: ReminderListElement){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addReminderListElement(reminderListElement)
        }
    }

    fun updateReminderListElement(reminderListElement: ReminderListElement){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateReminderListElement(reminderListElement)
        }
    }

    fun deleteReminderListElement(reminderListElement: ReminderListElement){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteReminderListElement(reminderListElement)
        }
    }
}