package com.jlp.unforgotchi.locations

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.jlp.unforgotchi.list.Lists
import com.jlp.unforgotchi.MainActivity
import com.jlp.unforgotchi.R
import com.jlp.unforgotchi.settings.Settings

class Locations : AppCompatActivity() , LocationsAdapter.OnItemClickListener {

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.locations_screen)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.locations_recycler_view)
        // this grid layout holds all the cards with the saved locations:
        recyclerview.layoutManager = GridLayoutManager(this, 2)
        //Add locations list:
        val locationsAdapter = LocationsAdapter(
            getInitialLocations(), this
        )
        // Setting the Adapter with the recyclerview
        recyclerview.adapter = locationsAdapter


        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)

        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // When the add-button is clicked, this Launcher will launch the Add-Activity.
        // The Add-Activity will then give back the result (i.e. the data of the new element)
        // which is then processed in the lambda here:
        var addLocationActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // This happens when the AddLocationActivity ends:
                val data: Intent? = result.data
                val newLocName: String? = data!!.getStringExtra("result")
                locationsAdapter.mList.add(
                    LocationItemsVM(
                        R.drawable.ic_baseline_location_city_24,
                        newLocName!!
                    )
                )
                locationsAdapter.notifyDataSetChanged()

            }
        }

        //when clicking the add-button:
        val addLocationButton: View = findViewById(R.id.add_location_button)
        addLocationButton.setOnClickListener {
            val addLocationIntent = Intent(this@Locations, AddLocationActivity::class.java)
            //startActivity(addLocationIntent) //<- this is the old way which doesn't give back a result
            addLocationActivityLauncher.launch(addLocationIntent)
        }

        //set intents to navigate to the other parts of the app:
        val homePage = Intent(this@Locations, MainActivity::class.java)
        val settingPage = Intent(this@Locations, Settings::class.java)
        val listsPage = Intent(this@Locations, Lists::class.java)
        val locationsPage = Intent(this@Locations, Locations::class.java)


        navView.setNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.nav_home -> startActivity(homePage)
                R.id.nav_lists -> startActivity(listsPage)
                R.id.nav_locations -> startActivity(locationsPage)
                R.id.nav_trash -> Toast.makeText(
                    applicationContext,
                    "Clicked placeholder",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.nav_settings -> startActivity(settingPage)
                R.id.nav_login -> Toast.makeText(
                    applicationContext,
                    "Clicked placeholder",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.nav_share -> Toast.makeText(
                    applicationContext,
                    "Clicked placeholder",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.nav_rate_us -> Toast.makeText(
                    applicationContext,
                    "Clicked placeholder",
                    Toast.LENGTH_SHORT
                ).show()

            }

            true

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(this, "Item $position clicked", Toast.LENGTH_SHORT).show()
    }
}