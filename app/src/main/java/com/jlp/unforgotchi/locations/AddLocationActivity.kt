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
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.jlp.unforgotchi.MainActivity
import com.jlp.unforgotchi.R
import com.jlp.unforgotchi.db.ReminderListViewModel
import java.io.FileDescriptor
import java.io.IOException


class AddLocationActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var addLocNameView: TextInputEditText

    private val previewImage by lazy { findViewById<ImageButton>(R.id.selected_location_image_button) }
    private var previewImageChanged : Boolean = false   //this is horrible coding dont copy this
    private var imageData : Uri? = null
    private val addWifiButton : Button by lazy { findViewById(R.id.add_wifi_to_location_button) }
    private var wifiName : String? = null

    var spinner:Spinner? = null

    //private val selectImageFromGalleryResult  = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
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

    //For the custom Dropdown Menu which allows for selecting multiple lists:
    //private val dropdownItems: MutableList<DropDownAdapter.DropDownItem<ReminderList>> = ArrayList()
    //private val selectedLists: MutableSet<ReminderList> = HashSet()
    var dropDownItems : MutableList<String> = ArrayList()
    //private val dropDownItemsAndIds : MutableList<Pair<String,Int>> = ArrayList()
    //private var listNameAndId = Pair("",0)
    private val dropDownIds : MutableList<Int> = ArrayList()
    private var listId = -2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_location_layout)
        addLocNameView = findViewById(R.id.add_name_of_location)
        previewImage.setImageResource(R.drawable.ic_baseline_image_search_24)
        previewImage.setOnClickListener {
            selectImageFromGallery()
        }

        addWifiButton.setOnClickListener{
            wifiName = MainActivity.getSsid(this)
            Toast.makeText(this,"Wifi added",Toast.LENGTH_SHORT).show()
        }

        //This is for the Dropdown Menu:
        val reminderListsVM: ReminderListViewModel = ViewModelProvider(this).get(ReminderListViewModel::class.java)

        Log.d("!!!!!! Laenge der Liste der ReminderListen Value: ","${reminderListsVM.readAllData.value?.size}")
        reminderListsVM.readAllData.observe(this) { reminderLists ->
            reminderLists.forEach{ element ->
                dropDownItems.add(element.listName)
                dropDownIds.add(element.id)
            }
            setupSpinner()
        }

        findViewById<FloatingActionButton>(R.id.finish_adding_location).setOnClickListener {//): ","${spinner.selectedItemPosition}")
            listId = dropDownIds[spinner!!.selectedItemPosition]
            Log.d("!!!!!! ListIds direkt vor processInput(): ","$listId")
            processInput()
        }

    }

    private fun setupSpinner() {
        spinner = findViewById<Spinner>(R.id.select_lists_spinner)
        spinner!!.onItemSelectedListener = this
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, dropDownItems)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.setAdapter(aa)
    }

    private fun processInput() {
        val intent = Intent()
        val name = addLocNameView.text.toString()

        if (name.isEmpty()) setResult(Activity.RESULT_CANCELED, intent)
        else createLocation(intent, name)

        finish()
    }

    private fun createLocation(intent: Intent, name: String) {
        Log.d("!!!!!! ListIds am Anfang von createLocation: ","$listId")
        if ( !(previewImageChanged) || previewImage.drawable == null) {
            previewImage.setImageResource(R.drawable.ic_baseline_location_city_24)
            intent.putExtra("name", name)
            intent.putExtra("wifiName", wifiName)
            //intent.putExtra("listId",listNameAndId[1])
            intent.putExtra("listId",listId)

        } else {
            intent.putExtra("name", name)
            intent.putExtra("image",imageData)
            intent.putExtra("wifiName", wifiName)
           // intent.putExtra("listId",listNameAndId[1])
            intent.putExtra("listId",listId)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            applicationContext.grantUriPermission("com.jlp.unforgotchi",imageData,
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        setResult(Activity.RESULT_OK, intent)
    }

    //This function is not used anymore. But because it was a pain to implement, it will remain here
    //just in case it's going to be needed in a future update.
    //This function converts an image Uri to a Bitmap
    private fun uriToBitmap(uri: Uri): Bitmap? {
        val scaledScreenWidth :Double = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val outMetrics = resources.displayMetrics
            outMetrics.widthPixels / 2.0
        } else {
            @Suppress("DEPRECATION")
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels / 2.0
        }

        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = Bitmap.createBitmap(BitmapFactory.decodeFileDescriptor(fileDescriptor))
            val imageheight = image.height.toFloat()
            val imagewidth = image.width.toFloat()
            val image2 = Bitmap.createScaledBitmap(
                BitmapFactory.decodeFileDescriptor(fileDescriptor),
                scaledScreenWidth.toInt(),
                (scaledScreenWidth * (imageheight / imagewidth)).toInt(),
                true
            )
            parcelFileDescriptor.close()
            return image2
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        TODO @laurin hier irgendwas mit item an position machen
        Log.d("#1#2#3#4################","${parent!!.getItemAtPosition(position)}")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}
