package com.jlp.unforgotchi.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.jlp.unforgotchi.db.ReminderList
import com.jlp.unforgotchi.db.ReminderListDatabase
import com.jlp.unforgotchi.db.ReminderListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderListViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<ReminderList>>
    //val getCount: LiveData<Int>

    private val repository: ReminderListRepository

    init {
        val userDao = ReminderListDatabase.getDatabase(application).reminderListDao()
        repository = ReminderListRepository(userDao)

        readAllData = repository.readAllData
        //getCount = repository.getCount

    }

    fun addReminderList(reminderList: ReminderList){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addReminderList(reminderList)
        }
    }

    /*fun updateReminderList(reminderList: ReminderList){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateReminderList(reminderList)
        }
    }*/

    /*fun deleteReminderList(reminderList: ReminderList){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteReminderList(reminderList)
        }
    }*/
}