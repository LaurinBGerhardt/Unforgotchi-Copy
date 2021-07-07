package com.jlp.unforgotchi.list

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.jlp.unforgotchi.*
import com.jlp.unforgotchi.db.ReminderList
import com.jlp.unforgotchi.db.ReminderListViewModel
import com.jlp.unforgotchi.detaillist.DetailList
import com.jlp.unforgotchi.locations.Locations
import com.jlp.unforgotchi.settings.Settings

class Lists : AppCompatActivity(), ListsAdapter.OnItemClickListener {

    private lateinit var mUserViewModel: ReminderListViewModel
    lateinit var toggle: ActionBarDrawerToggle
    private var arrayListNames = emptyArray<String>()
    private val adapter = ListsAdapter(/*arrayList, */this)

    //private var index: Int = 0
    private var edit = false
    private var delete = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lists)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.lists_recycler_view)
        // this grid layout holds all the cards with the saved locations:
        recyclerview.layoutManager = GridLayoutManager(this, 2)
        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter
        recyclerview.setHasFixedSize(true)

        mUserViewModel = ViewModelProvider(this).get(ReminderListViewModel::class.java)
        mUserViewModel.readAllData.observe(this, Observer { reminderList ->
            adapter.setData(reminderList)
        })

        //mUserViewModel.getCount.observe(this, Observer<Int> { integer -> index = integer })

        //when clicking the add-button:
        val addListsButton: View = findViewById(R.id.add_lists_button)
        addListsButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.add_listname_layout, null)
            val editText = dialogLayout.findViewById<EditText>(R.id.newListName)

            with(builder) {
                setTitle("New List")
                setPositiveButton("OK") { _, _ ->
                    insertList(editText.text.toString())
                }
                setNegativeButton("Cancel") { _, _ ->
                    Toast.makeText(applicationContext, "Cancel button clicked", Toast.LENGTH_SHORT)
                        .show()
                }
                setView(dialogLayout)
                show()
            }
        }

        //when clicking the delete-button:
        val deleteListsButton: View = findViewById(R.id.delete_lists_button)
        deleteListsButton.setOnClickListener {
            if (edit) {
                Toast.makeText(
                    applicationContext,
                    "Deactivate edit button first",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (delete) {
                    delete = false
                    deleteListsButton.setBackgroundColor(0xFF820333.toInt())
                } else {
                    delete = true
                    deleteListsButton.setBackgroundColor(0xFF000000.toInt())
                }
            }
        }

        //when clicking the edit button:
        val editListsButton: View = findViewById(R.id.edit_lists_button)
        editListsButton.setOnClickListener {
            if (delete) {
                Toast.makeText(
                    applicationContext,
                    "Deactivate delete button first",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (edit) {
                    edit = false
                    editListsButton.setBackgroundColor(0xFF820333.toInt())
                } else {
                    edit = true
                    editListsButton.setBackgroundColor(0xFF000000.toInt())
                }
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

    private fun insertList(listName: String) {
        if (!inputCheck(listName)) {
            Toast.makeText(
                applicationContext,
                "Please give input to create a list",
                Toast.LENGTH_SHORT
            ).show()
        } else if (adapter.itemCount >= 10) {
            Toast.makeText(
                applicationContext,
                "Can't create more than 10 lists",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // Create User Object
            val reminderList = ReminderList(0, listName, R.drawable.ic_baseline_list_alt_24)
            // Add Data to Database
            mUserViewModel.addReminderList(reminderList)
            arrayListNames += listName
            mUserViewModel.readAllData.observe(this, Observer { reminderList ->
                adapter.setData(reminderList)
            })
            //arrayList.add(reminderList)
            //mUserViewModel.getCount.observe(this, Observer<Int> { integer -> index = integer })
            Toast.makeText(applicationContext, "Successfully added!", Toast.LENGTH_LONG).show()
        }
    }

    private fun inputCheck(listName: String): Boolean {
        return !(TextUtils.isEmpty(listName))
    }

    private fun deleteList(position: Int) {
        // Create User Object
        val reminderList = ReminderList(
            position,
            arrayListNames[position],
            R.drawable.ic_baseline_list_alt_24
        )
        // Remove from Database
        mUserViewModel.deleteReminderList(reminderList)
        mUserViewModel.readAllData.observe(this, Observer { reminderList ->
            adapter.setData(reminderList)
        })
        arrayListNames.drop(position)
        Toast.makeText(applicationContext, "Successfully removed!", Toast.LENGTH_LONG).show()
    }

    private fun editList(listName: String, position: Int) {
        if (!inputCheck(listName)) {
            Toast.makeText(
                applicationContext,
                "Please give input to change the name",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // Create Reminder List Object
            val updatedReminderList = ReminderList(
                position,
                listName,
                R.drawable.ic_baseline_list_alt_24
            )
            // Update Current Object
            mUserViewModel.updateReminderList(updatedReminderList)
            arrayListNames[position] = listName
            mUserViewModel.readAllData.observe(this, Observer { reminderList ->
                adapter.setData(reminderList)
            })
            Toast.makeText(
                applicationContext,
                "Successfully updated!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onItemClick(position: Int) {
        when {
            edit -> {
                Toast.makeText(this, "Item $position clicked and edit set", Toast.LENGTH_SHORT)
                    .show()

                //open textDialog to adapt name
                val builder = AlertDialog.Builder(this)
                val inflater = layoutInflater
                val dialogLayout = inflater.inflate(R.layout.add_listname_layout, null)
                val editText = dialogLayout.findViewById<EditText>(R.id.newListName)

                with(builder) {
                    setTitle("Change List Name")
                    setPositiveButton("OK") { _, _ ->
                        editList(editText.text.toString(), position)
                    }
                    setNegativeButton("Cancel") { _, _ ->
                        Toast.makeText(
                            applicationContext,
                            "Cancel button clicked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    setView(dialogLayout)
                    show()
                }
            }
            delete -> {
                Toast.makeText(this, "Item $position deleted", Toast.LENGTH_SHORT).show()
                deleteList(position)
            }
            else -> {
                val i = Intent(this@Lists, DetailList::class.java)
                i.putExtra("position", position)
                startActivity(i)
            }
        }
    }
}