package com.jlp.unforgotchi.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderListElementViewModel(application: Application): AndroidViewModel(application) {

    var readAllElements: LiveData<List<ReminderListElement>>

    private val repository: ReminderListElementRepository

    init {
        val reminderListElementDao = ReminderListElementDatabase.getDatabase(application).reminderListElementDao()
        repository = ReminderListElementRepository(reminderListElementDao)

        readAllElements = repository.readAllElements

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