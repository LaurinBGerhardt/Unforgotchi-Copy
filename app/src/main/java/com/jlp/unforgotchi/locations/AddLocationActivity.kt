package com.jlp.unforgotchi.locations

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.jlp.unforgotchi.MainActivity
import com.jlp.unforgotchi.R
import com.jlp.unforgotchi.db.ReminderListViewModel


class AddLocationActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    //--LAYOUT-COMPONENTS--
    private val addLocNameView by lazy { findViewById<TextInputEditText>(R.id.add_name_of_location) }
    private val previewImage by lazy { findViewById<ImageButton>(R.id.selected_location_image_button) }
    private val addWifiButton  by lazy { findViewById<CheckBox>(R.id.add_wifi_to_location_button) }
    var spinner:Spinner? = null

    //--DATABASE-VIEWMODELS--
    //This is for the Dropdown Menu:
    private lateinit var reminderListsVM : ReminderListViewModel

    //--GLOBAL-VARIABLES--
    private var previewImageChanged : Boolean = false
    private var imageData : Uri? = null
    private var wifiName : String? = null
    // -2 ist a random unique nonsensical number so we know exactly where things went wrong:
    private var listId = -2

    //In a fututre update there is a custom dropdown menu planned which allows for selecting
    //multiple Lists. This was partially implemented.
    //The 4 following lines are necessary for that update:
    //private val dropdownItems: MutableList<DropDownAdapter.DropDownItem<ReminderList>> = ArrayList()
    //private val selectedLists: MutableSet<ReminderList> = HashSet()
    //private val dropDownItemsAndIds : MutableList<Pair<String,Int>> = ArrayList()
    //private var listNameAndId = Pair("",0)

    //This is how it's implemented currently:
    var dropDownItems : MutableList<String> = ArrayList()
    private val dropDownIds : MutableList<Int> = ArrayList()

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
        setContentView(R.layout.add_location_layout)

        setupListeners()

        reminderListsVM  = ViewModelProvider(this).get(ReminderListViewModel::class.java)

        previewImage.setImageResource(R.drawable.ic_baseline_image_search_24)

        reminderListsVM.readAllData.observe(this) { reminderLists ->
            reminderLists.forEach{ element ->
                dropDownItems.add(element.listName)
                dropDownIds.add(element.id)
            }
            setupSpinner()
        }

        /*
        //All this is for a better, custom dropdown menu, which will be implemented in a future update:
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Toast.makeText(this@AddLocationActivity,"You Should Select a WiFi",Toast.LENGTH_LONG).show()
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //listNameAndId = dropDownItemsAndIds[position]
                Log.d("!!!!!! Position in onItemSelected: ","$listId")
                Log.d("!!!!!! ListIds VOR assignment in onItemSelected: ","$listId")
                listId = dropDownIds[position]
                Log.d("!!!!!! ListIds NACH assignment in onItemSelected: ","$listId")
                findViewById<TextView>(R.id.select_list_text_view).text = dropDownItems[position]
            }
        }*/
    } //END onCreate

    private fun setupListeners() {
        //Selecting an image from the gallery when the button with search icon is pressed:
        previewImage.setOnClickListener {
            selectImageFromGallery()
        }

        //Add the currently connected wifi to the new location:
        addWifiButton.setOnClickListener{
            addWifiButton.isChecked = true
            wifiName = MainActivity.getSsid(this)
            Toast.makeText(this,"Wifi added",Toast.LENGTH_SHORT).show()
        }

        //When the plus floating action button is pressed, a new location will be created in the
        //Locations activity using the data which will be passed:
        findViewById<FloatingActionButton>(R.id.finish_adding_location).setOnClickListener {
            if (spinner!!.selectedItemPosition < 0) {
                Toast.makeText(this@AddLocationActivity,"Please Select A List",Toast.LENGTH_SHORT).show()
            } else {
                listId = dropDownIds[spinner!!.selectedItemPosition]
                processInput()
            }
        }
    }

    private fun setupSpinner() {
        spinner = findViewById<Spinner>(R.id.select_lists_spinner)
        spinner!!.onItemSelectedListener = this
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, dropDownItems)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.adapter = aa
    }

    //The input of the user is to be used to hand back data to the Locations Activity
    //so to create and add a new location:
    private fun processInput() {
        val intent = Intent()
        val name = addLocNameView.text.toString()

        if (name.isEmpty()) setResult(Activity.RESULT_CANCELED, intent)
        else giveBackLocationData(intent, name)

        finish()
    }

    //This method passes the data for the new location directly to the Locations activity.
    //This way of adding a new location is legacy code so to speak, because it's from before
    //a database was used. However, it still works just fine:
    private fun giveBackLocationData(intent: Intent, name: String) {
        intent.putExtra("wifiName", wifiName)
        intent.putExtra("name", name)
        if (listId >= 0){
            intent.putExtra("listId",listId)
        }
        if ( !(previewImageChanged) || previewImage.drawable == null) {
            previewImage.setImageResource(R.drawable.ic_baseline_location_city_24)
        } else {
            intent.putExtra("image",imageData)
            //All the following allow for the app not to loose permission to display the images:
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            applicationContext.grantUriPermission("com.jlp.unforgotchi",imageData,
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        setResult(Activity.RESULT_OK, intent)
    }

    //These two methods are from the AdapterView.OnItemSelectedListener:
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectListText : TextView = findViewById(R.id.select_a_list_spinner_text)
        selectListText.isGone = true
        listId = parent!!.getItemIdAtPosition(position).toInt()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //Nothing do to here
    }
}
