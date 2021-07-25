package com.jlp.unforgotchi.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LocationsViewModel(application: Application): AndroidViewModel(application) {

    val readAllLocations: LiveData<List<Location>>

    private val repository: LocationsRepository

    val getAllWifis : LiveData<List<String?>>

    init {
        val locationsDao = LocationsDatabase.getDatabase(application).locationsDao()
        repository = LocationsRepository(locationsDao)

        readAllLocations = repository.readAllLocations

        getAllWifis = repository.getAllWifis
    }

    fun addLocation(location: Location){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addLocation(location)
        }
    }

    //THIS IS THE FIRST WORKING ASYNC FUNCTION YAAAAAAY
    fun getLocationById(id : Int) : Location {
        var location : Location? = null
        runBlocking {
            location = async { repository.getLocationById(id) }.await()
        }
        return location ?: throw Exception("location is still null")
    }

    fun getLocations(): List<Location> {
        var locations : List<Location>? = null
        runBlocking {
            locations = async { repository.getLocations() }.await()
        }
        return locations ?: throw Exception("locations are still null")
    }

    fun updateLocation(location: Location,wifiName : String? = null){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLocation(location,wifiName)
        }
    }

    fun updateLocation(location: Location){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLocation(location)
        }
    }

    fun deleteLocation(location: Location){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteLocation(location)
        }
    }
}