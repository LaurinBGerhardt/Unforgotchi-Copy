package com.jlp.unforgotchi

//import com.google.android.gms.location.*
import android.Manifest.*
import android.Manifest.permission.*
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.jlp.unforgotchi.db.*
import com.jlp.unforgotchi.list.Lists
import com.jlp.unforgotchi.locations.Locations
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class MainActivity : AppCompatActivity() {

    //for the notification channel creation:
    private val CHANNEL_ID = "channel_id_test_01"

    //for the navigation:
    lateinit var toggle: ActionBarDrawerToggle

    //for the recyclerview to show current list:
    private val adapter = MainAdapter()

    //The table for special values (like the latest location):
    private lateinit var reminderListViewModel: ReminderListElementViewModel
    private val network = CheckWifi(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // for the navigation:
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        //set intents for the navigation
        val homePage = Intent(this@MainActivity, MainActivity::class.java)
        val listsPage = Intent(this@MainActivity, Lists::class.java)
        val locationsPage = Intent(this@MainActivity, Locations::class.java)
        val firstStepsPage = Intent(this@MainActivity, FirstSteps::class.java)

        reminderListViewModel =
            ViewModelProvider(this).get(ReminderListElementViewModel::class.java)
        specialValuesViewModel = ViewModelProvider(this).get(SpecialValuesViewModel::class.java)
        locationsViewModel = ViewModelProvider(this).get(LocationsViewModel::class.java)

        itemsToRemember = emptyArray<String>()

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> startActivity(homePage)
                R.id.nav_lists -> startActivity(listsPage)
                R.id.nav_locations -> startActivity(locationsPage)
                R.id.nav_first_steps -> startActivity(firstStepsPage)
            }
            true
        }

        recyclerViewSetup(firstStepsPage, reminderListViewModel.getElements(), getLatestLocation())

        askPermissions(
            arrayOf(ACCESS_FINE_LOCATION, ACCESS_NETWORK_STATE, CHANGE_WIFI_STATE)
        )

        network.registerNetworkCallback()
        createNotificationChannel()
        if (!network.isConnected) Toast.makeText(this, "Please enable Wifi", Toast.LENGTH_LONG)
            .show()

        /*
        //The timer has severe issues with the delay of the database, causing an infinite loop
        val timer = Timer("checkWifi", false)
        val locations = locationsViewModel.getLocations()
        // schedule at a fixed rate
        if (locations.isNotEmpty()) {
            timer.scheduleAtFixedRate(2000, 60000) {
                if (network.isConnected) setLatestLocation(
                    locations.filter {loc -> loc.wifiName == getSsid(applicationContext)})
            }
        } else
            timer.cancel()
        */
    }

    private fun getLatestLocation(): Location? {
        return locationsViewModel.getLocations()
            .filter { location -> location.location_id == specialValuesViewModel.getLatestLocationId() }
            .getOrNull(0)
    }

    companion object {
        private lateinit var itemsToRemember: Array<String>
        lateinit var locationsViewModel: LocationsViewModel
        lateinit var specialValuesViewModel: SpecialValuesViewModel

        fun updateLatestLocation(applicationContext: Context) {
            setLatestLocation(locationsViewModel.getLocations().filter {loc -> loc.wifiName == getSsid(applicationContext)})
        }

        fun setLatestLocation(locations: List<Location>) {
            if (locations.isEmpty()) return
            locations.forEach { location ->
                specialValuesViewModel.setSpecialValue(
                    SpecialValue(
                        ValueNames.LATEST_LOCATION.name,
                        location.location_id,
                        location.listId
                    )
                )
            }
        }

        fun getSsid(context: Context): String? {
            val wifiInfo = (context.getSystemService(WIFI_SERVICE) as WifiManager).connectionInfo
            return if (wifiInfo.supplicantState == SupplicantState.COMPLETED) {
                wifiInfo.ssid
            } else {
                null
            }
        }

        fun sendNotification(context: Context) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            val builder = NotificationCompat.Builder(context, "channel_id_test_01")
                .setSmallIcon(R.drawable.ic_unforgotchi)
                .setContentTitle("Don't forget to take:")
                .setContentText(itemsToRemember.joinToString(", "))
                .setContentIntent((pendingIntent))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            builder.setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                notify(101, builder.build())
            }
        }

        // helper function to strip the user input to a valid name throughout the app
        fun getValidInput(input : String) : String {
            return Regex("""\s+""")
                .replace(input.trim()," ")
                .filter { it.isLetterOrDigit() || it == ' ' || it in "._-–\"\'#^°*,/\\;!?+{[()]}"}
        }
    }

    override fun onResume() {
        super.onResume()
        val locations = locationsViewModel.getLocations()
        setLatestLocation(
            locations.filter {loc -> loc.wifiName == getSsid(applicationContext)})
    }

    override fun onDestroy() {
        super.onDestroy()
        val locations = locationsViewModel.getLocations()
        setLatestLocation(
            locations.filter {loc -> loc.wifiName == getSsid(applicationContext)})
    }

    private fun recyclerViewSetup(
        firstStepsPage: Intent,
        elements: List<ReminderListElement>,
        latestLocation: Location?
    ) {
        // for the recyclerview:
        val recyclerview = findViewById<RecyclerView>(R.id.lists_recycler_view)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter
        recyclerview.setHasFixedSize(true)
        //text which is shown instead of the recyclerview when recyclerview would be empty, leads to list page on click
        val noListsYetMessage = findViewById<LinearLayout>(R.id.noListsYetMessage)
        val firstStepsLink = findViewById<TextView>(R.id.firstSteps)
        firstStepsLink.setOnClickListener { startActivity(firstStepsPage) }
        // the Elements of which lists should be shown on the mainPage, initialized as the elements of the first list
        var listId : Int? = 1
        if (latestLocation != null) listId = latestLocation.listId
        //selects the right list and shows its element or the noListsYet View if no Elements in List
        val reminderListItems = mutableListOf<ReminderListElement>()
        elements.filter {elem -> elem.list == listId}.forEach {elem -> reminderListItems += elem}

        if (reminderListItems.isEmpty()) {
            noListsYetMessage.isVisible = true
            recyclerview.isVisible = false
        } else {
            noListsYetMessage.isVisible = false
            recyclerview.isVisible = true
            adapter.setData(reminderListItems)
            reminderListItems.forEach {element -> itemsToRemember += element.listElementName}
        }
    }

    private fun askPermissions(PERMISSIONS: Array<String>) {
        PERMISSIONS.forEach { permission ->
            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED)
                Log.d("Function askPermissions: ", "Permission" + permission + "already granted")
            else
                requestPermissions(arrayOf(permission), kotlin.math.abs(permission.hashCode()))
        }
    }

    @Override
//    TODO currently nonsensical; check for the proper hash code
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Unforgotchi Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "This is the Channel for all Unforgotchi Notifications"
            }
            val notificationManager: NotificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}