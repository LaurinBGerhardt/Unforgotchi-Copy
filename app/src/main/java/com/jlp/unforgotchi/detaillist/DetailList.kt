package com.jlp.unforgotchi.detaillist

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.jlp.unforgotchi.MainActivity
import com.jlp.unforgotchi.R
import com.jlp.unforgotchi.list.Lists
import com.jlp.unforgotchi.locations.Locations
import com.jlp.unforgotchi.settings.Settings

class DetailList : AppCompatActivity(), DetailListsAdapter.OnItemClickListener {

    lateinit var toggle : ActionBarDrawerToggle
    private val arrayList = generateList(500) //Creating an empty array-list
    private val adapter = DetailListsAdapter(arrayList, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_list_view)

        val bundle: Bundle? = intent.extras
        val position = intent.getIntExtra("position", 0)

        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)

        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.lists_recycler_view)
        // this grid layout holds all the cards with the saved locations:
        recyclerview.layoutManager = LinearLayoutManager(this)
        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter
        recyclerview.setHasFixedSize(true)

        //when clicking the add-button:
        val addItemButton: View = findViewById(R.id.add_item_button)
        addItemButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.add_listname_layout, null)
            val editText = dialogLayout.findViewById<EditText>(R.id.newListName)

            with(builder) {
                setTitle("New List")
                setPositiveButton("OK"){ dialog, which ->
                    insertList(editText.text.toString())
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
        val homePage = Intent(this@DetailList, MainActivity::class.java)
        val settingPage = Intent(this@DetailList, Settings::class.java)
        val listsPage = Intent(this@DetailList, Lists::class.java)
        val locationsPage = Intent(this@DetailList, Locations::class.java)



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

        val item = object : SwipeToDelete(this, 0, ItemTouchHelper.LEFT) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteList(viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper=ItemTouchHelper(item)
        itemTouchHelper.attachToRecyclerView(recyclerview)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun generateList(size: Int): ArrayList<DetailListsItemsVM> {

        val list: ArrayList<DetailListsItemsVM> = ArrayList()

        val element1 = DetailListsItemsVM("Key")

        val element2 = DetailListsItemsVM("Mobile Phone")
        list += element1
        list += element2
        return list
    }

    private fun insertList(listName: String){
        val newList = DetailListsItemsVM(
            listName
        )
        arrayList.add(newList)
        adapter.notifyDataSetChanged()
    }

    private fun deleteList(position: Int){
        if (position < arrayList.size) {
            arrayList.removeAt(position)
            adapter.notifyDataSetChanged()
        }
        else{
            Toast.makeText(applicationContext, "List not that long", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(this, "Item $position clicked", Toast.LENGTH_SHORT).show()
        val clickedItem = arrayList[position]

        //open textDialog to adapt name
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.add_listname_layout, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.newListName)

        with(builder) {
            setTitle("Change Items Name")
            setPositiveButton("OK"){ dialog, which ->
                clickedItem.text = editText.text.toString()
                adapter.notifyDataSetChanged()
            }
            setNegativeButton("Cancel"){ dialog, which ->
                Toast.makeText(applicationContext, "Cancel button clicked", Toast.LENGTH_SHORT).show()
            }
            setView(dialogLayout)
            show()
        }
    }
}