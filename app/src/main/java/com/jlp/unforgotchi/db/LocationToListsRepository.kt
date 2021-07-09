package com.jlp.unforgotchi.db

import androidx.lifecycle.LiveData


class LocationToListsRepository (private val locationToListsDao: LocationToListsDao){

    val readAll: LiveData<List<LocationToLists>> = locationToListsDao.readAll()

    suspend fun saveLocation(location: Location){
        locationToListsDao.saveLocation(location)
    }

    suspend fun saveLists(vararg reminderListElements: ReminderListElement){
        locationToListsDao.saveLists(*reminderListElements)
    }

    suspend fun updateLocation(location: Location){
        locationToListsDao.updateLocation(location)
    }

    suspend fun updateLists(vararg reminderListElements: ReminderListElement){
        locationToListsDao.updateLists(*reminderListElements)
    }

    suspend fun deleteLocation(location: Location){
        locationToListsDao.deleteLocation(location)
    }

    suspend fun deleteLists(vararg reminderListElements: ReminderListElement){
        locationToListsDao.deleteLists(*reminderListElements)
    }
}