package com.jlp.unforgotchi.locations

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.jlp.unforgotchi.FirstSteps
import com.jlp.unforgotchi.MainActivity
import com.jlp.unforgotchi.R
import com.jlp.unforgotchi.db.*
import com.jlp.unforgotchi.list.Lists

class Locations : AppCompatActivity() , LocationsAdapter.OnItemClickListener {

    //-LAYOUT-COMPONENTS--
    private val editOrDeleteLocationsButton: TextView by lazy { findViewById(R.id.edit_or_delete_locations_button) }
    val drawerLayout: DrawerLayout by lazy { findViewById(R.id.drawerLayout) }
    val navView: NavigationView by lazy { findViewById(R.id.nav_view) }
    val recyclerview : RecyclerView by lazy { findViewById(R.id.locations_recycler_view) }
    val addLocationButton: View by lazy { findViewById(R.id.add_location_button) }
    //This is for the Drawer Layout:
    private lateinit var toggle: ActionBarDrawerToggle

    //--DATABASE-VIEWMODELS--
    private lateinit var locationsDBViewModel: LocationsViewModel
    private lateinit var specialValuesViewModel: SpecialValuesViewModel

    //--GLOBAL-VARIABLES--
    // editLocationsMode always is the complement of deleteLocationsMode so to speak:
    private var editLocationsMode: Boolean = true
    private val EDIT = "Edit"
    private val DELETE = "Delete"
    //The List of Locations is in the LocationsAdapter:
    private val locationsAdapter = LocationsAdapter(
        mutableListOf<Location>(), this
    )

    ////////////////////////////////////////////////////
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.locations_screen)

        setupListeners()

        //Initialize databank viewmodels:
        locationsDBViewModel = ViewModelProvider(this).get(LocationsViewModel::class.java)
        specialValuesViewModel = ViewModelProvider(this).get(SpecialValuesViewModel::class.java)

        //setupRecyclerView:
        // this grid layout holds all the cards with the saved locations:
        recyclerview.layoutManager = GridLayoutManager(this, 2)
        // Setting the Adapter with the recyclerview
        recyclerview.adapter = locationsAdapter

        //The User can freely switch between editing Locations or deleting them:
        setUpEditOrDeleteMode()

        setupDrawer()

        //The Elements of the Locations Recycler View are from the Room Database:
        locationsDBViewModel.readAllLocations.observe(this, { locationsList ->
            locationsAdapter.setData(locationsList)
        })

    }//END onCreate

    private fun setupListeners() {
        // When the add-button is clicked, this Launcher will launch the Add-Activity.
        // The Add-Activity will then give back the result (i.e. the data of the new element)
        // which is then processed in the lambda here:
        val addLocationActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result -> processAddLocationResult(result) }
        //when clicking the add-button:
        addLocationButton.setOnClickListener {
            val addLocationIntent = Intent(this@Locations, AddLocationActivity::class.java)
            addLocationActivityLauncher.launch(addLocationIntent)
        }
    }

    private fun setupDrawer() {
        //This stuff is for the Drawer Layout:
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //set intents to navigate to the other parts of the app:
        val homePage = Intent(this@Locations, MainActivity::class.java)
        val listsPage = Intent(this@Locations, Lists::class.java)
        val locationsPage = Intent(this@Locations, Locations::class.java)
        val aboutUsPage = Intent(this@Locations, FirstSteps::class.java)


        //This is so one can navigate the entire app:
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> startActivity(homePage)
                R.id.nav_lists -> startActivity(listsPage)
                R.id.nav_locations -> startActivity(locationsPage)
                R.id.nav_first_steps -> startActivity(aboutUsPage)
            }
            true
        }
    }

    //The next two methods are from the LocationsAdapter.OnItemClickListener:
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    //If an item is clicked in edit mode, the EditLocationActivity will be launched for that item,
    //if it is clicked in delete mode, it will be removed from the locations list:
    override fun onItemClick(position: Int) {
        if (editLocationsMode){
            editLocation(position)
        } else {
            deleteLocation(position)
        }
    }

    //Starting the EditLocationActivity so one can modity the data of this Location (image, wifi, etc):
    private fun editLocation(position: Int) {
        val editLocation = Intent(this@Locations, EditLocationActivity::class.java)
        val selectedLocation = locationsAdapter.listOfLocations[position]
        val locationId = selectedLocation.location_id
        editLocation.putExtra("locationId", locationId)
        startActivity(editLocation)
    }

    //Removing the Location from the list of Locations in the adapter:
    private fun deleteLocation(position: Int) {
        val selectedLocation = locationsAdapter.listOfLocations[position]
        locationsDBViewModel.deleteLocation(selectedLocation)
        locationsAdapter.notifyItemRemoved(position)
    }

    //When the AddLocationActivity ends, a new Location is being added to the list of Locations,
    //if the result is set to RESULT_OK:
    private fun processAddLocationResult(result: ActivityResult){
        if (result.resultCode == RESULT_OK) {
            //Get the data for the new Location:
            val data: Intent? = result.data
            val newLocName: String = data!!.getStringExtra("name") ?: "New Location"
            val newWifiName : String? = data!!.getStringExtra("wifiName")
            val newImgData : Uri? = data!!.getParcelableExtra<Uri?>("image")
            val listId : Int = data!!.getIntExtra("listId",-5)

            //If the new Location has an image, we need to take the permission to display it:
            if (newImgData != null) {
                contentResolver.takePersistableUriPermission(
                    newImgData,
                    data.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            //Now all the data for the new Location is being used to create the Location object:
            val newLocation =
                Location(0, newLocName, newImgData?.toString(), newWifiName, listId)
            locationsDBViewModel.addLocation(newLocation)
            //The latest location is always the one wich was last connected to a wifi,
            //so if the new Locaiton has the current wifi, it is the latest one,
            //and the items in the home screen will be displayed accordingly.
            //The latest Location is stored between startups in the SpecialValues table:
            if (newWifiName != null) {
                specialValuesViewModel.setSpecialValue(
                    SpecialValue(
                        ValueNames.LATEST_LOCATION.name,
                        newLocName,
                        newLocation.listId
                    )
                )
            }
            locationsAdapter.notifyDataSetChanged()
        }
    }

    //Switching between edit mode and delete mode:
    private fun setUpEditOrDeleteMode(){
        if(editLocationsMode)   editOrDeleteLocationsButton.text = EDIT
        else                    editOrDeleteLocationsButton.text = DELETE
        editOrDeleteLocationsButton.setOnClickListener {
            if(editLocationsMode){
                //If it was edit mode, it's now delete mode:
                editLocationsMode = false
                editOrDeleteLocationsButton.text = DELETE
            } else {
                //If it was delete mode, it's now edit mode:
                editLocationsMode = true
                editOrDeleteLocationsButton.text = EDIT
            }
        }
    }
}