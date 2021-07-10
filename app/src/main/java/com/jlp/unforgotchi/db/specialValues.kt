package com.jlp.unforgotchi.db

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
}
