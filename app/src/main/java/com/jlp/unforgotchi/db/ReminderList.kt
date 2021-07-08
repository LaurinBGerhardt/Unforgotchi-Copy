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
        parentColumns = arrayOf("location_id"),
        childColumns = arrayOf("ref_to_location"),
        onDelete = ForeignKey.CASCADE
    )])
data class ReminderListElement(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var listElementName: String,
    var list: Int,
    @ColumnInfo(index = true, name = "ref_to_location")
    var ref_to_location : Int? = null
){
    constructor() : this(0, "", 0)
}

@Entity(tableName = "locations_table")
data class Location(
    @PrimaryKey(autoGenerate = true)
    var location_id: Int,
    var text: String,
    var image : String?
){
    constructor() : this(0, "",null)
}

data class LocationToLists(
    //@PrimaryKey(autoGenerate = true)
    //var locToListID : Int,
    @Embedded
    val location: Location,
    @Relation(
        parentColumn = "location_id",
        entityColumn = "ref_to_location",
        entity = ReminderListElement::class //Perhaps not needed
    )
    val lists: List<ReminderListElement>
)
