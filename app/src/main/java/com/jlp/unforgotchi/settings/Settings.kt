package com.jlp.unforgotchi.settings

import android.Manifest.permission.*
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.jlp.unforgotchi.MainActivity
import com.jlp.unforgotchi.R
import com.jlp.unforgotchi.db.Location
import com.jlp.unforgotchi.db.LocationsViewModel
import com.jlp.unforgotchi.list.Lists
import com.jlp.unforgotchi.locations.Locations


class Settings : AppCompatActivity() {

    lateinit var toggle : ActionBarDrawerToggle

    private val arrayList: ArrayList<String> = ArrayList()
    private var adapter: ArrayAdapter<*>? = null
    private lateinit var locations: List<Location>

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)
        val buttonScan: Button = findViewById(R.id.scanBtn)
        val listView: ListView = findViewById(R.id.wifiList)
        val wifiManager: WifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val wifiInfo: WifiInfo = wifiManager.connectionInfo
        val connManager: ConnectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val locationsDBViewModel : LocationsViewModel

        locationsDBViewModel = ViewModelProvider(this).get(LocationsViewModel::class.java)
        askPermissions(arrayOf(ACCESS_FINE_LOCATION, ACCESS_BACKGROUND_LOCATION, ACCESS_NETWORK_STATE, CHANGE_WIFI_STATE))


        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)

        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList)
        listView.adapter = adapter

        //set intents
        val homePage = Intent(this@Settings, MainActivity::class.java)
        val settingPage = Intent(this@Settings, Settings::class.java)
        val listsPage = Intent(this@Settings, Lists::class.java)
        val locationsPage = Intent(this@Settings, Locations::class.java)



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


        buttonScan.setOnClickListener{
            if (isWifiConnected(wifiManager, networkInfo)) {
                val ssid = getSsid(wifiInfo)
                if (ssid != null) arrayList.add(ssid)
                adapter?.notifyDataSetChanged()
            }
        }

        listView.setOnItemClickListener { parent, view, position, id ->
            val wifi: Any = listView.getItemAtPosition(position)
            Toast.makeText(baseContext, wifi.toString(), Toast.LENGTH_SHORT).show()
        }

        locationsDBViewModel.readAllLocations.observe(this,
            { locationsList ->
                locations = locationsList
                Log.d("###", locationsList.toString())

        })

    }

//    private fun getLocations(locationsList: List<Location>) {
//        for (location in locationsList) {
//            Log.d("###", location.toString())
//        }
//    }

    fun isWifiConnected(wifiManager: WifiManager, networkInfo: NetworkInfo?): Boolean {
        if (!wifiManager.isWifiEnabled) {
            Toast.makeText(this, "Please turn on Wifi and connect to a Network", Toast.LENGTH_LONG).show()
            return false;
        } else if (!networkInfo!!.isConnected) {
            Toast.makeText(this, "Please connect to a WiFi-Network", Toast.LENGTH_LONG).show()
            return false;
        } else {
            return true
        }
    }

     fun getSsid(wifiInfo: WifiInfo): String? {
        if (wifiInfo.supplicantState == SupplicantState.COMPLETED) {
            Toast.makeText(this, wifiInfo.ssid, Toast.LENGTH_LONG).show()
            return wifiInfo.ssid
        } else {
            return null
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

    /**
     * @return Boolean - true: if we have permissions, otherwise false
     */
    fun CheckPermissions():Boolean{
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    @Override
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show()
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
}