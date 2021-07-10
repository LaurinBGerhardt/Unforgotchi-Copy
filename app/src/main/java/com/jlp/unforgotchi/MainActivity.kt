package com.jlp.unforgotchi

//import com.google.android.gms.location.*
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
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
import com.jlp.unforgotchi.db.ReminderListElementViewModel
import com.jlp.unforgotchi.list.Lists
import com.jlp.unforgotchi.locations.Locations
import com.jlp.unforgotchi.settings.Settings

class MainActivity : AppCompatActivity() {

    //for the notifications:
    private val CHANNEL_ID = "channel_id_test_01"
    private val notificationId = 101
    private var elementsArray = emptyArray<String>()

    //for the navigation:
    lateinit var toggle : ActionBarDrawerToggle

    //for the location service, can be deleted if we don't use location:
//    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val PERMISSION_ID = 1010
//    lateinit var locationRequest: LocationRequest
    lateinit var lastLocation: Location
    lateinit var showLocation : TextView

    //for the recyclerview to show current list:
    private lateinit var detailListUserViewModel: ReminderListElementViewModel
    private val adapter = MainAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //create the one channel we use for all our notifications:
        createNotificationChannel()

        // for the navigation:
        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)
        //set intents for the navigation
        val homePage = Intent(this@MainActivity, MainActivity::class.java)
        val settingPage = Intent(this@MainActivity, Settings::class.java)
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
                R.id.nav_settings -> startActivity(settingPage)
                R.id.nav_login -> Toast.makeText(applicationContext, "Clicked placeholder", Toast.LENGTH_SHORT).show()
                R.id.nav_share -> Toast.makeText(applicationContext, "Clicked placeholder", Toast.LENGTH_SHORT).show()
                R.id.nav_rate_us -> Toast.makeText(applicationContext, "Clicked placeholder", Toast.LENGTH_SHORT).show()
            }
            true
        }

        //location Views, can be deleted if we don't use location
//        val getLocationButton = findViewById<Button>(R.id.getLocation)
//        getLocationButton.isVisible = false
//        showLocation = findViewById<TextView>(R.id.showLocation)
//        showLocation.isVisible = false
//        // for the location:
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//        getLocationButton.setOnClickListener {
//            showLocation.isVisible = true
//            Log.d("Debug:",CheckPermission().toString())
//            Log.d("Debug:",isLocationEnabled().toString())
//            RequestPermission()
//            getLastLocation()
//            //mit lastLocation.longitude/ latitude kann jetzt Standort check gemacht werden und entsprechende Liste geladen werden
//        }


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
            var x = 0
            while (x < elementsArray.size) {
                text += elementsArray[x] + "\n"
                x++
            }
            sendNotification(
                "Don't forget to take:",
                text
            )
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
        for (permission: String in PERMISSIONS) {
            if(ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                Log.d("###", "Permission"+permission+"Granted")
            } else {
                requestPermissions(arrayOf(permission), kotlin.math.abs(permission.hashCode()))
            }
        }
    }

    @Override
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
            val descriptionText = "This is the Channel for all Unforgotchi Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
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

    fun CheckPermission():Boolean{
        //this function will return a boolean
        //true: if we have permission
        //false if not
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    fun RequestPermission(){
        //this function will allows us to tell the user to request the necessary permission if they are not granted
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


//    fun isLocationEnabled():Boolean{
//        //this function will return to us the state of the location service
//        //if the gps or the network provider is enabled then it will return true otherwise it will return false
//        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//    }

//    fun getLastLocation(){
//        if(CheckPermission()){
//            if(isLocationEnabled()){
//                if (ActivityCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    return
//                }
//                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task->
//                    var location: Location? = task.result
//                    if(location == null){
//                        NewLocationData()
//                    }else{
//                        Log.d("Debug:" ,"Your Location:"+ location.longitude)
//                    }
//                }
//            }else{
//                Toast.makeText(this,"Please Turn on Your device Location",Toast.LENGTH_SHORT).show()
//            }
//        }else{
//            RequestPermission()
//        }
//    }


//    fun NewLocationData(){
//        var locationRequest =  LocationRequest()
//        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        locationRequest.interval = 0
//        locationRequest.fastestInterval = 0
//        locationRequest.numUpdates = 1
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {return
//        }
//        fusedLocationProviderClient!!.requestLocationUpdates(
//            locationRequest,locationCallback, Looper.myLooper()
//        )
//    }


//    private val locationCallback = object : LocationCallback(){
//        override fun onLocationResult(locationResult: LocationResult) {
//            lastLocation = locationResult.lastLocation
//            Log.d("Debug:","your last last location: "+ lastLocation.longitude.toString())
//            showLocation.text = "You Last Location is : Long: "+ lastLocation.longitude + " , Lat: " + lastLocation.latitude + "\n"
//        }
//    }
}