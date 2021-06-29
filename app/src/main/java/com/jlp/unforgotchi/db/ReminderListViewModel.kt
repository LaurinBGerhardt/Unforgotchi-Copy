package com.jlp.unforgotchi.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderListViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<ReminderList>>
    private val repository: ReminderListRepository

    init {
        val userDao = ReminderListDatabase.getDatabase(application).reminderListDao()
        repository = ReminderListRepository(userDao)
        readAllData = repository.readAllData
    }

    fun addReminderList(reminderList: ReminderList){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addReminderList(reminderList)
        }
    }
}