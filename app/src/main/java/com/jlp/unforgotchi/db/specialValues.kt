package com.jlp.unforgotchi.db

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

enum class ValueNames{
    LATEST_LOCATION
}

@Dao
interface SpecialValuesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSpecialValue(specialValue: SpecialValue)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSpecialValue(specialValue: SpecialValue)

    @Delete
    suspend fun deleteSpecialValue(specialValue: SpecialValue)

    @Query("SELECT * FROM special_values")
    fun readAllSpecialValues(): LiveData<List<SpecialValue>>

    @Query("Select locationId FROM special_values")
    suspend fun getLatestLocationId(): Int
}

class SpecialValueRepository(private val specialValuesDao : SpecialValuesDao){
    val readAllSpecialValues : LiveData<List<SpecialValue>> = specialValuesDao.readAllSpecialValues()

    suspend fun addSpecialValue(specialValue: SpecialValue){
        specialValuesDao.addSpecialValue(specialValue)
    }

    suspend fun updateSpecialValue(specialValue: SpecialValue){
        specialValuesDao.updateSpecialValue(specialValue)
    }

    suspend fun deleteSpecialValue(specialValue: SpecialValue){
        specialValuesDao.deleteSpecialValue(specialValue)
    }

    suspend fun getLatestLocationId(): Int {
        return specialValuesDao.getLatestLocationId()
    }
}

@Database(entities = [SpecialValue::class], version = 1, exportSchema = false)
abstract class SpecialValuesDatabase : RoomDatabase() {
    abstract fun specialValuesDao(): SpecialValuesDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: SpecialValuesDatabase? = null

        fun getDatabase(context: Context): SpecialValuesDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SpecialValuesDatabase::class.java,
                    "SpecialValues_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

class SpecialValuesViewModel(application: Application): AndroidViewModel(application){
    val readAllSpecialValues : LiveData<List<SpecialValue>>
    private val repository : SpecialValueRepository

    init {
        val specialValuesDao = SpecialValuesDatabase.getDatabase(application).specialValuesDao()
        repository = SpecialValueRepository(specialValuesDao)
        readAllSpecialValues = repository.readAllSpecialValues
    }

    fun updateSpecialValue(specialValue: SpecialValue){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateSpecialValue(specialValue)
        }
    }

    fun setSpecialValue(specialValue: SpecialValue){
        viewModelScope.launch(Dispatchers.IO){
            repository.addSpecialValue(specialValue)
        }
    }

    fun deleteSpecialValue(specialValue: SpecialValue){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteSpecialValue(specialValue)
        }
    }

    fun getLatestLocationId(): Int? {
        var id : Int? = null
        runBlocking {
            id = async { repository.getLatestLocationId() }.await()
        }
        return id
    }
}
