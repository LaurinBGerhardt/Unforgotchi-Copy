package com.jlp.unforgotchi

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.jlp.unforgotchi.locations.AddLocationActivity
import com.jlp.unforgotchi.locations.Locations
import com.jlp.unforgotchi.locations.LocationsAdapter
import com.jlp.unforgotchi.locations.getInitialLocations
import com.jlp.unforgotchi.settings.Settings

class Lists : AppCompatActivity() {

    lateinit var toggle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lists)
        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)
        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.lists_recycler_view)
        // this grid layout holds all the cards with the saved locations:
        recyclerview.layoutManager = GridLayoutManager(this,2)
        //Add locations list:
        val listsAdapter = ListsAdapter(
            getInitialLists()
        )
        // Setting the Adapter with the recyclerview
        recyclerview.adapter = listsAdapter

        //when clicking the add-button:
        val addListsButton: View = findViewById(R.id.add_lists_button)
        addListsButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.add_listname_layout, null)
            val editText = dialogLayout.findViewById<EditText>(R.id.newListName)

            with(builder) {
                setTitle("New List")
                setPositiveButton("OK"){dialog, which ->
                    Toast.makeText(applicationContext, editText.text.toString(), Toast.LENGTH_SHORT).show()
                }
                setNegativeButton("Cancel"){ dialog, which ->
                    Toast.makeText(applicationContext, "Cancel button clicked", Toast.LENGTH_SHORT).show()
                }
                setView(dialogLayout)
                show()
            }
        }

        //all down here for the navigation menu

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)

        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //set intents
        val homePage = Intent(this@Lists, MainActivity::class.java)
        val settingPage = Intent(this@Lists, Settings::class.java)
        val listsPage = Intent(this@Lists, Lists::class.java)
        val locationsPage = Intent(this@Lists, Locations::class.java)



        navView.setNavigationItemSelectedListener {

            when(it.itemId){

                R.id.nav_home -> startActivity(homePage)
                R.id.nav_lists -> startActivity(listsPage)
                R.id.nav_locations -> startActivity(locationsPage)
                R.id.nav_trash -> Toast.makeText(applicationContext, "Clicked placeholder", Toast.LENGTH_SHORT).show()
                R.id.nav_settings -> startActivity(settingPage)
                R.id.nav_login -> Toast.makeText(applicationContext, "Clicked placeholder", Toast.LENGTH_SHORT).show()
                R.id.nav_share -> Toast.makeText(applicationContext, "Clicked placeholder", Toast.LENGTH_SHORT).show()
                R.id.nav_rate_us -> Toast.makeText(applicationContext, "Clicked placeholder", Toast.LENGTH_SHORT).show()

            }

            true

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}