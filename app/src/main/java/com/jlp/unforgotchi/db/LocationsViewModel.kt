package com.jlp.unforgotchi.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationsViewModel(application: Application): AndroidViewModel(application) {

    val readAllLocations: LiveData<List<Location>>

    private val repository: LocationsRepository

    init {
        val locationsDao = LocationsDatabase.getDatabase(application).locationsDao()
        repository = LocationsRepository(locationsDao)

        readAllLocations = repository.readAllLocations
    }

    fun addLocation(location: Location){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addLocation(location)
        }
    }
}