package com.jlp.unforgotchi.detaillist

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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.jlp.unforgotchi.MainActivity
import com.jlp.unforgotchi.R
import com.jlp.unforgotchi.db.ReminderList
import com.jlp.unforgotchi.db.ReminderListElement
import com.jlp.unforgotchi.db.ReminderListElementViewModel
import com.jlp.unforgotchi.list.Lists
import com.jlp.unforgotchi.locations.Locations
import com.jlp.unforgotchi.settings.Settings

class DetailList : AppCompatActivity(), DetailListsAdapter.OnItemClickListener {

    lateinit var toggle : ActionBarDrawerToggle

    private lateinit var mUserViewModel: ReminderListElementViewModel
    private val adapter = DetailListsAdapter(this)
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_list_view)

        // for the navigation menu
        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //set intents
        val homePage = Intent(this@DetailList, MainActivity::class.java)
        val settingPage = Intent(this@DetailList, Settings::class.java)
        val listsPage = Intent(this@DetailList, Lists::class.java)
        val locationsPage = Intent(this@DetailList, Locations::class.java)

        navView.setNavigationItemSelectedListener {

            when(it.itemId){

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

        // getting information which list was clicked
        val bundle: Bundle? = intent.extras
        position = intent.getIntExtra("position", 0)


        // for the recyclerview
        val recyclerview = findViewById<RecyclerView>(R.id.lists_recycler_view)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter
        recyclerview.setHasFixedSize(true)

        // show the element of the current list
        mUserViewModel = ViewModelProvider(this).get(ReminderListElementViewModel::class.java)
        if(position==0){
            mUserViewModel.readAllElementsFromList1.observe(this, Observer { reminderListElement ->
                adapter.setData(reminderListElement)
            })
        }
        if(position==1){
            mUserViewModel.readAllElementsFromList2.observe(this, Observer { reminderListElement ->
                adapter.setData(reminderListElement)
            })
        }
        if(position==2){
            mUserViewModel.readAllElementsFromList3.observe(this, Observer { reminderListElement ->
                adapter.setData(reminderListElement)
            })
        }
        if(position==3){
            mUserViewModel.readAllElementsFromList4.observe(this, Observer { reminderListElement ->
                adapter.setData(reminderListElement)
            })
        }
        if(position==4){
            mUserViewModel.readAllElementsFromList5.observe(this, Observer { reminderListElement ->
                adapter.setData(reminderListElement)
            })
        }
        if(position==5){
            mUserViewModel.readAllElementsFromList6.observe(this, Observer { reminderListElement ->
                adapter.setData(reminderListElement)
            })
        }
        if(position==6){
            mUserViewModel.readAllElementsFromList7.observe(this, Observer { reminderListElement ->
                adapter.setData(reminderListElement)
            })
        }
        if(position==7){
            mUserViewModel.readAllElementsFromList8.observe(this, Observer { reminderListElement ->
                adapter.setData(reminderListElement)
            })
        }
        if(position==8){
            mUserViewModel.readAllElementsFromList9.observe(this, Observer { reminderListElement ->
                adapter.setData(reminderListElement)
            })
        }
        if(position==9){
            mUserViewModel.readAllElementsFromList10.observe(this, Observer { reminderListElement ->
                adapter.setData(reminderListElement)
            })
        }


        //when clicking the add-button:
        val addItemButton: View = findViewById(R.id.add_item_button)
        addItemButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.add_listitem_layout, null)
            val editText = dialogLayout.findViewById<EditText>(R.id.newListItem)

            with(builder) {
                setTitle("New List Item")
                setPositiveButton("OK"){ _, _ ->
                    insertListItem(editText.text.toString())
                }
                setNegativeButton("Cancel"){ _, _ ->
                    Toast.makeText(applicationContext, "Cancel button clicked", Toast.LENGTH_SHORT).show()
                }
                setView(dialogLayout)
                show()
            }
        }

        // functions to build in swipe functionality
        val item = object : SwipeToDelete(this, 0, ItemTouchHelper.LEFT) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteListElement(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper=ItemTouchHelper(item)
        itemTouchHelper.attachToRecyclerView(recyclerview)
    }

    // for navigation
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // add Item to List
    private fun insertListItem(listElementName: String){
        if (!inputCheck(listElementName)){
            Toast.makeText(
                applicationContext,
                "Please give input to create a list",
                Toast.LENGTH_SHORT
            ).show()
        }
        else {
            val reminderListElement = ReminderListElement(0, listElementName, position)
            mUserViewModel.addReminderListElement(reminderListElement)
            Toast.makeText(applicationContext, "Successfully added!", Toast.LENGTH_LONG).show()
        }
    }

    // help function to verify whether input is given
    private fun inputCheck(listName: String): Boolean{
        return !(TextUtils.isEmpty(listName))
    }

    // delete element:
    private fun deleteListElement(positionItem: Int){
        // Create User Object
        val reminderListElement = ReminderListElement(
            positionItem + 1,
            "",
            0
        )
        // Remove from Database
        mUserViewModel.deleteReminderListElement(reminderListElement)
        Toast.makeText(applicationContext, "Successfully removed!", Toast.LENGTH_LONG).show()
    }

    // change name of List Element
    private fun editList(listElementName: String, positionInList: Int) {
        if (!inputCheck(listElementName)) {
            Toast.makeText(
                applicationContext,
                "Please give input to change the name",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // Create Reminder List Element Object
            val updatedReminderListElement = ReminderListElement(
                positionInList +1,
                listElementName,
                position
            )
            // Update Current Object
            mUserViewModel.updateReminderListElement(updatedReminderListElement)
            Toast.makeText(
                applicationContext,
                "Successfully updated!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // opens window on item clicked to change name of clicked item
    override fun onItemClick(position: Int) {
        Toast.makeText(this, "Item $position clicked", Toast.LENGTH_SHORT).show()

        //open textDialog to adapt name
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.add_listitem_layout, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.newListItem)

        with(builder) {
            setTitle("Change Items Name")
            setPositiveButton("OK") { _, _ ->
                editList(editText.text.toString(), position)
            }
            setNegativeButton("Cancel"){ _, _ ->
                Toast.makeText(applicationContext, "Cancel button clicked", Toast.LENGTH_SHORT).show()
            }
            setView(dialogLayout)
            show()
        }
    }
}