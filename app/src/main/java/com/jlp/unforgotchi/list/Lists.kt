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

class Lists : AppCompatActivity(), ListsAdapter.OnItemClickListener {

    private lateinit var mUserViewModel: ReminderListViewModel
    lateinit var toggle: ActionBarDrawerToggle
    private val adapter = ListsAdapter(this)

    private var edit = false
    private var delete = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lists)

        // for the navigation menu
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //set intents
        val homePage = Intent(this@Lists, MainActivity::class.java)
        val listsPage = Intent(this@Lists, Lists::class.java)
        val locationsPage = Intent(this@Lists, Locations::class.java)
        // set click listeners
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

        // for recyclerview
        val recyclerview = findViewById<RecyclerView>(R.id.lists_recycler_view)
        recyclerview.layoutManager = GridLayoutManager(this, 2)
        recyclerview.adapter = adapter
        recyclerview.setHasFixedSize(true)

        // set Data out of database
        mUserViewModel = ViewModelProvider(this).get(ReminderListViewModel::class.java)
        mUserViewModel.readAllData.observe(this, Observer { reminderList ->
            adapter.setData(reminderList)
        })

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

    }

    // for navigation:
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // function to add a new list
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
            val reminderList = ReminderList(0, listName, R.drawable.ic_baseline_list_alt_24)
            mUserViewModel.addReminderList(reminderList)
            mUserViewModel.readAllData.observe(this, Observer { reminderList ->
                adapter.setData(reminderList)
            })
            Toast.makeText(applicationContext, "Successfully added!", Toast.LENGTH_LONG).show()
        }
    }

    // helper function to verify if something has been entered
    private fun inputCheck(listName: String): Boolean {
        return !(TextUtils.isEmpty(listName))
    }

    // function to remove a list
    private fun deleteList(position: Int) {
        val reminderList = ReminderList(
            position + 1,
            "",
            R.drawable.ic_baseline_list_alt_24
        )
        mUserViewModel.deleteReminderList(reminderList)
        mUserViewModel.readAllData.observe(this, Observer { reminderList ->
            adapter.setData(reminderList)
        })
        Toast.makeText(applicationContext, "Successfully removed!", Toast.LENGTH_LONG).show()
    }

    // function to change the name of a list
    private fun editList(listName: String, position: Int) {
        if (!inputCheck(listName)) {
            Toast.makeText(
                applicationContext,
                "Please give input to change the name",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val updatedReminderList = ReminderList(
                position +1,
                listName,
                R.drawable.ic_baseline_list_alt_24
            )
            mUserViewModel.updateReminderList(updatedReminderList)
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

    // handle on item clicks depending on which buttons are clicked
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