package com.jlp.unforgotchi

//import com.google.android.gms.location.*
import android.Manifest
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
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.jlp.unforgotchi.db.*
import com.jlp.unforgotchi.list.Lists
import com.jlp.unforgotchi.locations.Locations
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    //for the notifications:
    private val CHANNEL_ID = "channel_id_test_01"
    private val notificationId = 101
    private var elementsArray = emptyArray<String>()

    //for the navigation:
    lateinit var toggle : ActionBarDrawerToggle

    //for the recyclerview to show current list:
    private lateinit var detailListUserViewModel: ReminderListElementViewModel
    private val adapter = MainAdapter()

    //The table for special values (like the latest location):
    private lateinit var specialValuesViewModel: SpecialValuesViewModel
    private lateinit var locationsViewModel: LocationsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.Companion.context = this
        setContentView(R.layout.activity_main)

        //The table for special values (like the latest location):
        specialValuesViewModel = ViewModelProvider(this).get(SpecialValuesViewModel::class.java)
        locationsViewModel = ViewModelProvider(this).get(LocationsViewModel::class.java)
        //create the one channel we use for all our notifications:
        createNotificationChannel()

        // for the navigation:
        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)
        //set intents for the navigation
        val homePage = Intent(this@MainActivity, MainActivity::class.java)
        val listsPage = Intent(this@MainActivity, Lists::class.java)
        val locationsPage = Intent(this@MainActivity, Locations::class.java)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_home -> startActivity(homePage)
                R.id.nav_lists -> startActivity(listsPage)
                R.id.nav_locations -> startActivity(locationsPage)
                R.id.nav_trash -> Toast.makeText(applicationContext, "Clicked placeholder", Toast.LENGTH_SHORT).show()
                R.id.nav_login -> Toast.makeText(applicationContext, "Clicked placeholder", Toast.LENGTH_SHORT).show()
                R.id.nav_share -> Toast.makeText(applicationContext, "Clicked placeholder", Toast.LENGTH_SHORT).show()
                R.id.nav_rate_us -> Toast.makeText(applicationContext, "Clicked placeholder", Toast.LENGTH_SHORT).show()
            }
            true
        }

        recyclerViewSetup(listsPage)

        askPermissions(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
        ))

        // for the notification:
        val notificationButton = findViewById<Button>(R.id.reminder_notification)
        notificationButton.setOnClickListener {
            var text = ""
            elementsArray.forEach { element -> text += element + "\n" }
            sendNotification("Don't forget to take:", text)
        }
//        setLatestLocation()
        val network = CheckWifi(this)
        network.registerNetworkCallback()

        if (isConnected){
            Toast.makeText(this,"Connected",Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this,"FOOOOO",Toast.LENGTH_LONG).show()
        }
    }

    private fun setLatestLocation(location: Location) {
        specialValuesViewModel.setSpecialValue(
            SpecialValue(
                ValueNames.LATEST_LOCATION.name,
                location.text
            )
        )
    }

//    private fun isConnectedToWifi(): Boolean {
//        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        var isWifiConn: Boolean = false
//        connMgr.allNetworks.forEach { network ->
//            connMgr.getNetworkInfo(network)?.apply {
//                if (type == ConnectivityManager.TYPE_WIFI) {
//                    isWifiConn = isWifiConn or isConnected
//                }
//            }
//        }
//        return isWifiConn
//    }

    private fun getLatestLocation(): com.jlp.unforgotchi.db.Location? {
        var latestLocation: List<com.jlp.unforgotchi.db.Location>? = null
        locationsViewModel.readAllLocations.observe(this, {
                locations -> latestLocation = locations.filter { containsWifi(it, getSsid(this)) }
        })
        return latestLocation?.get(0)
    }

    private fun containsWifi(location: com.jlp.unforgotchi.db.Location, ssid: String?): Boolean {
        return if (ssid != null) {
            location.wifiName == ssid
        } else false
    }

    companion object {
        //Ignore warning; Context is held in inner class anyways; see for yourself
        //https://stackoverflow.com/questions/54075649/access-application-context-in-companion-object-in-kotlin
        private lateinit var context: Context
        var isConnected: Boolean by Delegates.observable(false) { property, oldValue, newValue ->
        //  TODO hier notification schicken
            Log.d("Observable property: ", property.toString())
            Log.d("Observable old: ", oldValue.toString())
            Log.d("Observable new: ", newValue.toString())
        }

        fun getSsid(con: Context): String? {
            context = con
            val wifiInfo = (context.getSystemService(WIFI_SERVICE) as WifiManager).connectionInfo
            return if (wifiInfo.supplicantState == SupplicantState.COMPLETED) {
                wifiInfo.ssid
            } else {
                null
            }
        }

        class Status {
            var isConnected: Boolean by Delegates.observable(false) { property, oldValue, newValue ->

            }
        }
    }

    private fun recyclerViewSetup(listsPage: Intent) {
        // for the recyclerview:
        val recyclerview = findViewById<RecyclerView>(R.id.lists_recycler_view)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter
        recyclerview.setHasFixedSize(true)
        //text which is shown instead of the recyclerview when recyclerview would be empty, leads to list page on click
        val noListsYet = findViewById<TextView>(R.id.noListsYet)
        noListsYet.setOnClickListener(){
            startActivity(listsPage)
        }
        // the Elements of which lists should be shown on the mainPage, initialized as the elements of the first list
        var position = 0
        //selects the right list and shows its element or the noListsYet View if no Elements in List
        detailListUserViewModel = ViewModelProvider(this).get(ReminderListElementViewModel::class.java)
        if(position==0){
            detailListUserViewModel.readAllElementsFromList1.observe(this, Observer { reminderListElement ->
                if (reminderListElement.isEmpty()){
                    noListsYet.isVisible = true
                    recyclerview.isVisible = false
                }
                else {
                    noListsYet.isVisible = false
                    recyclerview.isVisible = true
                    adapter.setData(reminderListElement)
                    reminderListElement.forEach { element ->
                        elementsArray += element.listElementName
                    }
                }
            })
        }
        if(position==1){
            detailListUserViewModel.readAllElementsFromList2.observe(this, Observer { reminderListElement ->
                if (reminderListElement.isEmpty()){
                    noListsYet.isVisible = true
                    recyclerview.isVisible = false
                }
                else {
                    noListsYet.isVisible = false
                    recyclerview.isVisible = true
                    adapter.setData(reminderListElement)
                }
            })
        }
        if(position==2){
            detailListUserViewModel.readAllElementsFromList3.observe(this, Observer { reminderListElement ->
                if (reminderListElement.isEmpty()){
                    noListsYet.isVisible = true
                    recyclerview.isVisible = false
                }
                else {
                    noListsYet.isVisible = false
                    recyclerview.isVisible = true
                    adapter.setData(reminderListElement)
                }
            })
        }
        if(position==3){
            detailListUserViewModel.readAllElementsFromList4.observe(this, Observer { reminderListElement ->
                if (reminderListElement.isEmpty()){
                    noListsYet.isVisible = true
                    recyclerview.isVisible = false
                }
                else {
                    noListsYet.isVisible = false
                    recyclerview.isVisible = true
                    adapter.setData(reminderListElement)
                }
            })
        }
        if(position==4){
            detailListUserViewModel.readAllElementsFromList5.observe(this, Observer { reminderListElement ->
                if (reminderListElement.isEmpty()){
                    noListsYet.isVisible = true
                    recyclerview.isVisible = false
                }
                else {
                    noListsYet.isVisible = false
                    recyclerview.isVisible = true
                    adapter.setData(reminderListElement)
                }
            })
        }
        if(position==5){
            detailListUserViewModel.readAllElementsFromList6.observe(this, Observer { reminderListElement ->
                if (reminderListElement.isEmpty()){
                    noListsYet.isVisible = true
                    recyclerview.isVisible = false
                }
                else {
                    noListsYet.isVisible = false
                    recyclerview.isVisible = true
                    adapter.setData(reminderListElement)
                }
            })
        }
        if(position==6){
            detailListUserViewModel.readAllElementsFromList7.observe(this, Observer { reminderListElement ->
                if (reminderListElement.isEmpty()){
                    noListsYet.isVisible = true
                    recyclerview.isVisible = false
                }
                else {
                    noListsYet.isVisible = false
                    recyclerview.isVisible = true
                    adapter.setData(reminderListElement)
                }
            })
        }
        if(position==7){
            detailListUserViewModel.readAllElementsFromList8.observe(this, Observer { reminderListElement ->
                if (reminderListElement.isEmpty()){
                    noListsYet.isVisible = true
                    recyclerview.isVisible = false
                }
                else {
                    noListsYet.isVisible = false
                    recyclerview.isVisible = true
                    adapter.setData(reminderListElement)
                }
            })
        }
        if(position==8){
            detailListUserViewModel.readAllElementsFromList9.observe(this, Observer { reminderListElement ->
                if (reminderListElement.isEmpty()){
                    noListsYet.isVisible = true
                    recyclerview.isVisible = false
                }
                else {
                    noListsYet.isVisible = false
                    recyclerview.isVisible = true
                    adapter.setData(reminderListElement)
                }
            })
        }
        if(position==9){
            detailListUserViewModel.readAllElementsFromList10.observe(this, Observer { reminderListElement ->
                if (reminderListElement.isEmpty()){
                    noListsYet.isVisible = true
                    recyclerview.isVisible = false
                }
                else {
                    noListsYet.isVisible = false
                    recyclerview.isVisible = true
                    adapter.setData(reminderListElement)
                }
            })
        }
    }

    private fun askPermissions(PERMISSIONS: Array<String>) {
        PERMISSIONS.forEach { permission ->
            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                Log.d("###", "Permission" + permission + "Granted")
            } else {
                requestPermissions(arrayOf(permission), kotlin.math.abs(permission.hashCode()))
            }
        }
    }

    @Override
//    TODO currently nonsensical; check for the proper hash code
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Unforgotchi Notification Channel"
            val channel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "This is the Channel for all Unforgotchi Notifications"
            }
            val notificationManager: NotificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(title: String, description: String) {

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(description)
            .setContentIntent((pendingIntent))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        builder.setAutoCancel(true);

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }
//    TODO check for permissions in Bugfxing phase
//    fun CheckPermission():Boolean{
//        //returns true: if we have permission, false if not
//        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
//            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
//            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED){
//            return true
//        }
//        return false
//    }
}