package com.jlp.unforgotchi.db

import androidx.lifecycle.LiveData


class LocationsRepository(private val locationsDao: LocationsDao) {

    val readAllLocations: LiveData<List<Location>> = locationsDao.readAllLocations()

    suspend fun addLocation(location: Location){
        locationsDao.addLocation(location)
    }
}