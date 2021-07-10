package com.jlp.unforgotchi.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

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
