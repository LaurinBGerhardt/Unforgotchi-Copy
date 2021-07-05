package com.jlp.unforgotchi.db

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder_list_table")
data class ReminderList(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var listName: String,
    val image: Int
){
    constructor() : this(0, "", 0)
}

@Entity(tableName = "reminder_list_elements_table")
data class ReminderListElement(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var listElementName: String
){
    constructor() : this(0, "")
}

@Entity(tableName = "locations_table")
data class Location(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var text: String,
    var image : String?
){
    constructor() : this(0, "",null)
}