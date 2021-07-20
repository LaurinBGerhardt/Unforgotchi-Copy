package com.jlp.unforgotchi.locations

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isGone
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.jlp.unforgotchi.MainActivity
import com.jlp.unforgotchi.R
import com.jlp.unforgotchi.db.Location
import com.jlp.unforgotchi.db.LocationsViewModel
import com.jlp.unforgotchi.db.ReminderListViewModel
import com.jlp.unforgotchi.db.SpecialValuesViewModel

class EditLocationActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    //The Location this is all about:
    private lateinit var currentLocation : Location

    //All the components of the layout:
    private val previewImage by lazy { findViewById<ImageButton>(R.id.edit_location_image_button) }
    private val editWifiButton : CheckBox by lazy { findViewById(R.id.edit_wifi_to_location_button) }
    private val editLocNameView: TextInputEditText by lazy { findViewById(R.id.edit_name_of_location) }
    var spinner: Spinner? = null

    //All the database viewmodels:
    private lateinit var locationsViewModel : LocationsViewModel
    private lateinit var specialValuesViewModel : SpecialValuesViewModel
    private lateinit var reminderListsVM: ReminderListViewModel

    //Handy global variables:
    private var previewImageChanged : Boolean = false
    private var imageData : Uri? = null
    private var connectedWifi : String? = null
    var dropDownItems : MutableList<String> = ArrayList()
    private val dropDownIds : MutableList<Int> = ArrayList()
    private var listId = -2
    private var wifiChanged = false

    //Making selecting image form gallery possible:
    private val selectImageFromGalleryResult  = registerForActivityResult(RetreiveImageContract()) { uri: Uri? ->
        this.applicationContext.grantUriPermission("com.jlp.unforgotchi",uri,
            Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        uri?.let {
            previewImage.setImageURI(uri)
            imageData = uri//.path
            previewImageChanged = true
        }
    }
    private fun selectImageFromGallery()  {
        selectImageFromGalleryResult.launch("image/*")
    }

    //////////////////////////////////////////////////////
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_location_layout)
        //editLocNameView = findViewById(R.id.edit_name_of_location)

        //Initializing databank viewmodels:
        locationsViewModel = ViewModelProvider(this).get(LocationsViewModel::class.java)
        specialValuesViewModel = ViewModelProvider(this).get(SpecialValuesViewModel::class.java)
        reminderListsVM = ViewModelProvider(this).get(ReminderListViewModel::class.java)

        val currentLocationId = intent.getIntExtra("locationId",-8)
        Log.d("######################## current Location Id given to Edit Location Activity: ","$currentLocationId")
        if(currentLocationId <0) throw Exception("Invalid Location Id was given to the EditLocationActivity")
        currentLocation = locationsViewModel.getLocationById(currentLocationId)
        connectedWifi = MainActivity.getSsid(this)

        val locationImage : Uri? = currentLocation.image?.toUri()
        val locationWifi : String? = currentLocation.wifiName

        editWifiButton.isChecked = locationWifi.equals(connectedWifi)
        editWifiButton.setOnClickListener{
            wifiChanged = !locationWifi.equals(connectedWifi) && editWifiButton.isChecked
        }

        editLocNameView.setText(currentLocation.text)

        if (locationImage != null) {
            contentResolver.takePersistableUriPermission(
                locationImage,
                intent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            previewImage.setImageURI(locationImage)
        } else {
            previewImage.setImageResource(R.drawable.ic_baseline_image_search_24)
        }

        previewImage.setOnClickListener {
            selectImageFromGallery()
        }

        //This is for the Dropdown Menu:
        Log.d("!!!!!! Laenge der Liste der ReminderListen Value: ","${reminderListsVM.readAllData.value?.size}")
        reminderListsVM.readAllData.observe(this) { reminderLists ->
            reminderLists.forEach{ element ->
                dropDownItems.add(element.listName)
                dropDownIds.add(element.id)
            }
            setupSpinner()
        }

        findViewById<FloatingActionButton>(R.id.finish_editing_location).setOnClickListener {
            if (spinner!!.selectedItemPosition < 0) {
                Toast.makeText(this@EditLocationActivity,"Please Select A List",Toast.LENGTH_SHORT).show()
            } else {
                listId = dropDownIds[spinner!!.selectedItemPosition]
                Log.d("!!!!!! ListIds direkt vor processInput(): ", "$listId")
                saveChanges()
            }
        }

    } //END onCreate

    private fun setupSpinner() {
        spinner = findViewById<Spinner>(R.id.edit_lists_spinner)
        spinner!!.onItemSelectedListener = this
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, dropDownItems)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.adapter = aa
    }

    private fun saveChanges() {
        val name = editLocNameView.text.toString()

        if (name.isEmpty()) setResult(Activity.RESULT_CANCELED, intent)

        val replacementLocation = Location(
            currentLocation.location_id,
            editLocNameView.text.toString(),
            if(previewImageChanged) imageData?.toString() else currentLocation.image,
            if(editWifiButton.isChecked) connectedWifi else null,
            listId
        )

        locationsViewModel.updateLocation(replacementLocation)

        finish()
    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectListText : TextView = findViewById(R.id.edit_a_list_spinner_text)
        selectListText.isGone = true
        Log.d("#1#2#3#4################","${parent!!.getItemAtPosition(position)}")
        //listId = parent!!.getItemIdAtPosition(position).toInt()
        //Log.d("#1#2#3#4################ ListId: ","${listId}")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}