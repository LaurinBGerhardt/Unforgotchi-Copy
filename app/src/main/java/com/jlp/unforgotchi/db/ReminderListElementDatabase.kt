package com.jlp.unforgotchi.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ReminderListElement::class,Location::class], version = 1, exportSchema = false)
abstract class ReminderListElementDatabase : RoomDatabase() {
    abstract fun reminderListElementDao(): ReminderListElementDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ReminderListElementDatabase? = null

        fun getDatabase(context: Context): ReminderListElementDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReminderListElementDatabase::class.java,
                    "Items_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
