package com.jlp.unforgotchi.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.jlp.unforgotchi.MainActivity
import com.jlp.unforgotchi.R
import com.jlp.unforgotchi.list.Lists
import com.jlp.unforgotchi.locations.Locations


class Settings : AppCompatActivity() {

    lateinit var toggle : ActionBarDrawerToggle

    private val arrayList: ArrayList<String> = ArrayList()
    private var adapter: ArrayAdapter<*>? = null
    private val wifiManager: WifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
    private val wifiInfo: WifiInfo = wifiManager.connectionInfo
    private val connManager: ConnectivityManager = applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkInfo: NetworkInfo? = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)
        val buttonScan: Button = findViewById(R.id.scanBtn)
        val listView: ListView = findViewById(R.id.wifiList)

        if (!CheckPermissions()) askPermission(arrayOf(
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION
        ))



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
            if (isWifiConnected()) {
                val macAddress = getMacAddress()
                if (macAddress != null) arrayList.add(macAddress)
                adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun isWifiConnected(): Boolean {
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

    private fun getMacAddress (): String? {
        if (wifiInfo.supplicantState == SupplicantState.COMPLETED) {
            return wifiInfo.macAddress
        } else {
            return "MAC Address not available"
        }
    }

    private fun askPermission(PERMISSIONS: Array<String>) {
        for (permission: String in PERMISSIONS) {
            if(ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Permission"+permission+"Granted",Toast.LENGTH_SHORT).show()
            } else {
                requestPermissions(arrayOf(permission), permission.hashCode())
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
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
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
}