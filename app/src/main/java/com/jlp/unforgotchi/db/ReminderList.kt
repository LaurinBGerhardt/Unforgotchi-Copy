package com.jlp.unforgotchi.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder_list_table")
data class ReminderList(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val listName: String,
    val image: Int
){
    constructor() : this(0, "", 0)
}