package com.jlp.unforgotchi.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Location::class], version = 1, exportSchema = false)
abstract class LocationsDatabase : RoomDatabase() {
    abstract fun locationsDao(): LocationsDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: LocationsDatabase? = null

        fun getDatabase(context: Context): LocationsDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocationsDatabase::class.java,
                    "Locations_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
