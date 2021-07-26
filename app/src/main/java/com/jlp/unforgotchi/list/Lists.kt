package com.jlp.unforgotchi.list

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.jlp.unforgotchi.*
import com.jlp.unforgotchi.db.ReminderList
import com.jlp.unforgotchi.db.ReminderListElementViewModel
import com.jlp.unforgotchi.db.ReminderListViewModel
import com.jlp.unforgotchi.detaillist.DetailList
import com.jlp.unforgotchi.locations.Locations

class Lists : AppCompatActivity(), ListsAdapter.OnItemClickListener {

    private lateinit var itemViewModel: ReminderListElementViewModel
    private lateinit var listViewModel: ReminderListViewModel
    lateinit var toggle: ActionBarDrawerToggle
    private val adapter = ListsAdapter(this)

    private enum class Mode(val string:String){
        EDIT("Edit Name"),
        DELETE("Delete"),
        ADDITEM("Show List")
    }
    private var currentMode = Mode.ADDITEM

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
        val aboutUsPage = Intent(this@Lists, FirstSteps::class.java)

        // set click listeners
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> startActivity(homePage)
                R.id.nav_lists -> startActivity(listsPage)
                R.id.nav_locations -> startActivity(locationsPage)
                R.id.nav_first_steps -> startActivity(aboutUsPage)
            }
            true
        }

        // for recyclerview
        val recyclerview = findViewById<RecyclerView>(R.id.lists_recycler_view)
        recyclerview.layoutManager = GridLayoutManager(this, 2)
        recyclerview.adapter = adapter
        recyclerview.setHasFixedSize(true)

        // set Data out of database
        itemViewModel = ViewModelProvider(this).get(ReminderListElementViewModel::class.java)
        listViewModel = ViewModelProvider(this).get(ReminderListViewModel::class.java)
        listViewModel.readAllData.observe(this, { reminderList ->
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
                    Toast.makeText(applicationContext, "No changes made", Toast.LENGTH_SHORT)
                        .show()
                }
                setView(dialogLayout)
                show()
            }
        }

        //when clicking the mode-button:
        val modeButton : TextView = findViewById(R.id.switch_mode_of_lists_screen_button)
        modeButton.text = currentMode.string
        modeButton.setOnClickListener {
            when(currentMode){
                Mode.EDIT       -> {
                    currentMode = Mode.DELETE
                    modeButton.text = currentMode.string
                }
                Mode.DELETE     -> {
                    currentMode = Mode.ADDITEM
                    modeButton.text = currentMode.string
                }
                Mode.ADDITEM    -> {
                    currentMode = Mode.EDIT
                    modeButton.text = currentMode.string
                }
            }
        }
    }

    // for navigation:
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true

        return super.onOptionsItemSelected(item)
    }

    // function to add a new list
    private fun insertList(input: String) {
        val listName = getValidInput(input)
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
            listViewModel.addReminderList(reminderList)
            listViewModel.readAllData.observe(this, { reminderList ->
                adapter.setData(reminderList)
            })
            Toast.makeText(applicationContext, "Successfully added!", Toast.LENGTH_LONG).show()
        }
    }

    // helper function to verify if something has been entered
    private fun inputCheck(listName: String): Boolean {
        return !(TextUtils.isEmpty(listName))
    }

    // helper function to strip the user input to a valid name
    private fun getValidInput(input: String): String {
        return Regex("""\s+""")
            .replace(input.trim()," ")
            .filter { it.isLetterOrDigit() || it == ' ' }
    }

    // function to remove a list
    private fun deleteList(position: Int) {
        var array = emptyArray<ReminderList>()
        listViewModel.readAllData.observe(this, { reminderList ->
            array += reminderList
        })
        val clickedListId = array[position].id
        val clickedListName = array[position].listName
        val reminderList = ReminderList(
            clickedListId,
            clickedListName,
            R.drawable.ic_baseline_list_alt_24
        )
        listViewModel.deleteReminderList(reminderList)
        listViewModel.readAllData.observe(this, { reminderList ->
            adapter.setData(reminderList)
        })
        Toast.makeText(applicationContext, "Successfully removed!", Toast.LENGTH_LONG).show()
    }

    // function to change the name of a list
    private fun editList(input: String, position: Int) {
        val listName = getValidInput(input)
        var array = emptyArray<ReminderList>()
        listViewModel.readAllData.observe(this, { reminderList ->
            array += reminderList
        })
        val clickedListId = array[position].id
        if (!inputCheck(listName)) {
            Toast.makeText(
                applicationContext,
                "Please give input to change the name",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val updatedReminderList = ReminderList(
                clickedListId,
                listName,
                R.drawable.ic_baseline_list_alt_24
            )
            listViewModel.updateReminderList(updatedReminderList)
            listViewModel.readAllData.observe(this, { reminderList ->
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
        when (currentMode) {
            Mode.EDIT -> {
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
            Mode.DELETE -> {
                Toast.makeText(this, "Item $position deleted", Toast.LENGTH_SHORT).show()
                deleteList(position)
            }
            else -> {
                val i = Intent(this@Lists, DetailList::class.java)
                var list = listOf<ReminderList>()
                listViewModel.readAllData.observe(this, { reminderList ->
                    list = reminderList
                })
                i.putExtra("position", list[position].id)
                startActivity(i)
            }
        }
    }
}