package com.jlp.unforgotchi.db

import androidx.lifecycle.LiveData


class LocationsRepository(private val locationsDao: LocationsDao) {

    val readAllLocations: LiveData<List<Location>> = locationsDao.readAllLocations()

    suspend fun addLocation(location: Location){
        locationsDao.addLocation(location)
    }

    suspend fun updateLocation(location: Location){
        locationsDao.updateLocation(location)
    }

    suspend fun updateLocation(location: Location, wifiName : String?){
        val replacementLocation = Location(location.location_id, location.text,location.image,wifiName)
        locationsDao.updateLocation(replacementLocation)
    }

    suspend fun deleteLocation(location: Location){
        locationsDao.deleteLocation(location)
    }
}