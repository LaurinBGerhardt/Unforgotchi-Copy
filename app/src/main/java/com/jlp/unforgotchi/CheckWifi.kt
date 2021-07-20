package com.jlp.unforgotchi

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkInfo
import android.net.NetworkRequest
import kotlin.properties.Delegates


class CheckWifi constructor(applicationContext: Context) {
    val applicationContext: Context = applicationContext
    var isConnected: Boolean by Delegates.observable(false) { property, oldValue, newValue ->

            if (!newValue && newValue != oldValue) MainActivity.sendNotification(applicationContext)
        }
    fun registerNetworkCallback() {
        try {
            val connectivityManager = applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val builder = NetworkRequest.Builder()
            builder.build()
            connectivityManager.registerDefaultNetworkCallback(object : NetworkCallback() {
                override fun onAvailable(network: Network) {
                    val networkInfo: NetworkInfo? = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    if(networkInfo?.isConnected == true) isConnected = true
                }

                override fun onLost(network: Network) {
                    isConnected = false
                }
            })
            isConnected = false
        } catch (e: Exception) {
            isConnected = false
        }
    }
}