package com.jlp.unforgotchi.locations

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
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
import com.jlp.unforgotchi.db.*
import java.io.FileDescriptor
import java.io.IOException

class EditLocationActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    //The Location this is all about:
    private lateinit var currentLocation : Location

    //--LAYOUT-COMPONENTS--
    private val previewImage by lazy { findViewById<ImageButton>(R.id.edit_location_image_button) }
    private val editWifiButton : CheckBox by lazy { findViewById(R.id.edit_wifi_to_location_button) }
    private val editLocNameView: TextInputEditText by lazy { findViewById(R.id.edit_name_of_location) }
    var spinner: Spinner? = null

    //--DATABASE-VIEWMODELS--
    private lateinit var locationsViewModel : LocationsViewModel
    private lateinit var specialValuesViewModel : SpecialValuesViewModel
    private lateinit var reminderListsVM: ReminderListViewModel

    //--GLOBAL-VARIABLES--
    private var previewImageChanged : Boolean = false
    private var imageData : Uri? = null
    private var connectedWifi : String? = null
    var dropDownItems : MutableList<String> = ArrayList()
    private val dropDownIds : MutableList<Int> = ArrayList()
    // -7 is a unique nonsensical number so we know exactly where things went wrong:
    private var listId = -7

    //Making selecting image form gallery possible:
    private val selectImageFromGalleryResult  = registerForActivityResult(RetreiveImageContract())
    { uri: Uri? ->
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

    ////////////////////////////////////////////////////
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_location_layout)

        setupListeners()

        //Initializing databank viewmodels:
        locationsViewModel = ViewModelProvider(this).get(LocationsViewModel::class.java)
        specialValuesViewModel = ViewModelProvider(this).get(SpecialValuesViewModel::class.java)
        reminderListsVM = ViewModelProvider(this).get(ReminderListViewModel::class.java)

        //Getting the currently connected wifi:
        connectedWifi = MainActivity.getSsid(this)

        //Getting the location and its data which is to be edited:
        val currentLocationId = intent.getIntExtra("locationId",-8)
        if(currentLocationId <0) throw Exception("Invalid Location Id was given to the EditLocationActivity")
        currentLocation = locationsViewModel.getLocationById(currentLocationId)
        val locationImage : Uri? = currentLocation.image?.toUri()
        val locationWifi : String? = currentLocation.wifiName

        //If the current wifi is already attached to the location, it will be displayed:
        editWifiButton.isChecked = locationWifi.equals(connectedWifi)

        editLocNameView.setText(currentLocation.text)

        if (locationImage != null) {
            // For some very weird reason the permissions are buggy
            try {
                contentResolver.takePersistableUriPermission(
                    locationImage,
                    intent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                previewImage.setImageURI(locationImage)
            } catch (e: SecurityException){
                previewImage.setImageResource(R.drawable.ic_baseline_location_city_24)
            }
            //This would be the work-around, which itself is a bit buggy,
            //which is why the try-catch is used:
            //val imageBitMap = uriToBitmap(locationImage)
            //previewImage.setImageBitmap(imageBitMap)
        } else {
            //If this location doesn't have an image:
            previewImage.setImageResource(R.drawable.ic_baseline_image_search_24)
        }

        //Displaying the items in the dropdown menu correctly:
        Log.d("!!!!!! Laenge der Liste der ReminderListen Value: ","${reminderListsVM.readAllData.value?.size}")
        reminderListsVM.readAllData.observe(this) { reminderLists ->
            reminderLists.forEach{ element ->
                dropDownItems.add(element.listName)
                dropDownIds.add(element.id)
            }
            setupSpinner()
        }

    } //END onCreate

    private fun setupListeners() {
        //Select a new Image when clicking the old one:
        previewImage.setOnClickListener {
            selectImageFromGallery()
        }

        //Save and exit activity when clicking the tick floating action button:
        findViewById<FloatingActionButton>(R.id.finish_editing_location).setOnClickListener {
            if (spinner!!.selectedItemPosition < 0) {
                Toast.makeText(this@EditLocationActivity,"Please Select A List",Toast.LENGTH_SHORT).show()
            } else {
                listId = dropDownIds[spinner!!.selectedItemPosition]
                saveChanges()
            }
        }
    }

    private fun setupSpinner() {
        spinner = findViewById<Spinner>(R.id.edit_lists_spinner)
        spinner!!.onItemSelectedListener = this
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dropDownItems)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.adapter = arrayAdapter
    }

    private fun saveChanges() {
        val name = MainActivity.getValidInput(editLocNameView.text.toString())

        if (!name.isEmpty()) {

            val newWifiName = if (editWifiButton.isChecked) connectedWifi else null

            //Before adding the new Location we HAVE to make sure that Locations which already
            //have the new wifi get their wifi set to null:
            if(newWifiName != null) locationsViewModel.removeWifi(newWifiName)

            val replacementLocation = Location(
                currentLocation.location_id,
                name,
                if (previewImageChanged) imageData?.toString() else currentLocation.image,
                newWifiName,
                listId
            )

            locationsViewModel.updateLocation(replacementLocation)

            if(editWifiButton.isChecked) {
                specialValuesViewModel.updateSpecialValue(
                    SpecialValue(
                        ValueNames.LATEST_LOCATION.name,
                        replacementLocation.location_id,
                        replacementLocation.listId
                    )
                )
            }

            finish()
        } else {
            Toast.makeText(this@EditLocationActivity,"Please Input A Name",Toast.LENGTH_SHORT).show()
        }
    }


    //These two methods are from the AdapterView.OnItemSelectedListener,
    //which is there for the spinner
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectListText : TextView = findViewById(R.id.edit_a_list_spinner_text)
        selectListText.isGone = true
    }
    override fun onNothingSelected(parent: AdapterView<*>?) {
        //Nothing to do here
    }

    //This function converts an image Uri to a Bitmap
    //This is necessary because the permissions are buggy which would cause the app to crash
    //from time to time when editing a Location
    private fun uriToBitmap(uri: Uri?): Bitmap? {
        //The higher the compression factor is, the faster the EditLocationActivity will start,
        //but the poorer the quality of the image will be.
        //We decided to go for a slightly longer waiting period.
        val COMPRESSION_FACTOR = 1.1
        if (uri == null) return null
        val scaledScreenWidth :Double = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val outMetrics = resources.displayMetrics
            outMetrics.widthPixels / COMPRESSION_FACTOR
        } else {
            @Suppress("DEPRECATION")
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels / COMPRESSION_FACTOR
        }

        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = Bitmap.createBitmap(BitmapFactory.decodeFileDescriptor(fileDescriptor))
            val imageHeight = image.height.toFloat()
            val imageWidth = image.width.toFloat()
            val compressedImage = Bitmap.createScaledBitmap(
                BitmapFactory.decodeFileDescriptor(fileDescriptor),
                scaledScreenWidth.toInt(),
                (scaledScreenWidth * (imageHeight / imageWidth)).toInt(),
                true
            )
            parcelFileDescriptor.close()
            return compressedImage
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}