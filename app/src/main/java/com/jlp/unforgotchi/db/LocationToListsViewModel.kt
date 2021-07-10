package com.jlp.unforgotchi.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationToListsViewModel (application: Application) : AndroidViewModel(application) {
    val readAllLocationToLists: LiveData<List<LocationToLists>>

    private val repository: LocationToListsRepository

    init {
        val locationToListsDao =
            LocationToListsDatabase.getDatabase(application).locationToListsDao()
        repository = LocationToListsRepository(locationToListsDao)

        readAllLocationToLists = repository.readAll
    }

    fun saveLocation(location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveLocation(location)
        }
    }

    fun updateLocation(location: Location, wifiName : String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLocation(location, wifiName)
        }
    }

    fun deleteLocation(location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteLocation(location)
        }
    }

    fun saveLists(vararg reminderListElements: ReminderListElement){
        viewModelScope.launch(Dispatchers.IO){
            repository.saveLists(*reminderListElements)
        }
    }

    fun updateLists(vararg reminderListElements: ReminderListElement){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateLists(*reminderListElements)
        }
    }

    fun deleteLists(vararg reminderListElements: ReminderListElement){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteLists(*reminderListElements)
        }
    }
}