package com.jlp.unforgotchi.db

/*
//In a future update a locations will be able to be associated with multiple lists.
//This feature was already half-implemented, and there's no reason to delete the code which is
//already there.

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Dao
interface LocationToListsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveLocation(location: Location)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveLists(vararg reminderListElements: ReminderListElement)

    @Transaction
    @Query("SELECT * FROM locations_table ORDER BY location_id ASC")
    fun readAll(): LiveData<List<LocationToLists>>

    @Transaction
    @Query("SELECT * FROM locations_table WHERE location_id = :id")
    suspend fun getByLocationId(id: Int): LocationToLists

    //I'm not quite sure whether the following ones will work:

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLocation(location: Location)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLists(vararg reminderListElements: ReminderListElement)

    @Delete
    suspend fun deleteLocation(location: Location)

    @Delete
    suspend fun deleteLists(vararg reminderListElements: ReminderListElement)

}

@Database(entities = [Location::class,ReminderListElement::class], version = 1, exportSchema = false)
abstract class LocationToListsDatabase : RoomDatabase() {
    abstract fun locationToListsDao(): LocationToListsDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: LocationToListsDatabase? = null

        fun getDatabase(context: Context): LocationToListsDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocationToListsDatabase::class.java,
                    "LocationToLists_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

class LocationToListsRepository (private val locationToListsDao: LocationToListsDao){

    val readAll: LiveData<List<LocationToLists>> = locationToListsDao.readAll()

    suspend fun saveLocation(location: Location){
        locationToListsDao.saveLocation(location)
    }

    suspend fun saveLists(vararg reminderListElements: ReminderListElement){
        locationToListsDao.saveLists(*reminderListElements)
    }

    suspend fun updateLocation(location: Location, wifiName : String?){
        val replacementLocation = Location(location.location_id, location.text,location.image,wifiName, location.listId)
        locationToListsDao.updateLocation(replacementLocation)
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
 */