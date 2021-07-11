package com.jlp.unforgotchi

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkInfo
import android.net.NetworkRequest


class CheckWifi constructor(applicationContext: Context) {
    // You need to pass the context when creating the class
    val applicationContext: Context = applicationContext
    // Network Check
    fun registerNetworkCallback() {
        try {
            val connectivityManager = applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val builder = NetworkRequest.Builder()
            builder.build()
            connectivityManager.registerDefaultNetworkCallback(object : NetworkCallback() {
                override fun onAvailable(network: Network) {
                    val networkInfo: NetworkInfo? = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    if(networkInfo?.isConnected == true) MainActivity.isConnected = true
                }

                override fun onLost(network: Network) {
                    MainActivity.isConnected = false
                }
            })
            MainActivity.isConnected = false
        } catch (e: Exception) {
            MainActivity.isConnected = false
        }
    }
}