package com.jlp.unforgotchi.settings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.jlp.unforgotchi.MainActivity
import com.jlp.unforgotchi.R
import com.jlp.unforgotchi.list.Lists
import com.jlp.unforgotchi.locations.Locations


class Settings : AppCompatActivity() {

    lateinit var toggle : ActionBarDrawerToggle

    private val wifiManager: WifiManager? = null
    private val arrayList: ArrayList<String> = ArrayList()
    private var adapter: ArrayAdapter<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)
        val buttonScan: Button = findViewById(R.id.scanBtn)
        val listView: ListView = findViewById(R.id.wifiList)
        val wifiManager: WifiManager = getSystemService(WIFI_SERVICE) as WifiManager

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)

        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

//        buttonScan.setOnClickListener{
//                scanWifi()
//        }

        if (!wifiManager.isWifiEnabled) {
            Toast.makeText(this, "WiFi is disabled ... Please enable it", Toast.LENGTH_LONG).show()
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList)
        listView.adapter = adapter
//        scanWifi()

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        this.registerReceiver(wifiScanReceiver, intentFilter)

        val success = wifiManager.startScan()
        if (!success) {
            // scan failure handling
            scanFailure()
        }

    }

    private fun scanWifi() {
        arrayList.clear()
        registerReceiver(wifiScanReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        wifiManager?.startScan()
//        Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show()
    }

    private val wifiScanReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            print("ONRECIEVE")
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            if (success) {
                scanSuccess()
            } else {
                scanFailure()
            }
        }
    }

    private fun scanSuccess() {
        val results = wifiManager?.scanResults
        Toast.makeText(this, "close", Toast.LENGTH_SHORT).show()
        if (results != null) {
            Toast.makeText(this, "MADEIT", Toast.LENGTH_SHORT).show()
            for (scanResult: ScanResult in results) {
                arrayList.add(scanResult.SSID + " - " + scanResult.capabilities)
                adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        val results = wifiManager?.scanResults
        Toast.makeText(this, "Scanning failed", Toast.LENGTH_SHORT).show()
//        ... potentially use older scan results ...
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}