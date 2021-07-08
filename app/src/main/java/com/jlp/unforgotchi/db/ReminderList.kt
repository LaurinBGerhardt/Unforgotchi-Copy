package com.jlp.unforgotchi.db

import android.net.Uri
import androidx.room.*

@Entity(tableName = "reminder_list_table")
data class ReminderList(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var listName: String,
    val image: Int
){
    constructor() : this(0, "", 0)
}

@Entity(tableName = "reminder_list_elements_table",
    foreignKeys = [ForeignKey(
        entity = Location::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("location"),
        onDelete = ForeignKey.CASCADE
    )])
data class ReminderListElement(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var listElementName: String,
    var list: Int,
    var location : Int? = null
){
    constructor() : this(0, "", 0)
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

data class LocationToLists(
    @Embedded
    val location: Location,
    @Relation(
        parentColumn = "id",
        entityColumn = "location"
    )
    val lists: List<ReminderListElement>
)
