package com.jlp.unforgotchi.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ReminderList::class], version = 1, exportSchema = false)
abstract class ReminderListDatabase : RoomDatabase() {
    abstract fun reminderListDao(): ReminderListDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ReminderListDatabase? = null

        fun getDatabase(context: Context): ReminderListDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReminderListDatabase::class.java,
                    "List_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
