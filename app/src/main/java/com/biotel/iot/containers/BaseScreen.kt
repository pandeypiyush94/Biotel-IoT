package com.biotel.iot.containers

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.biotel.iot.App
import com.biotel.iot.ui.main.View
import com.biotel.iot.util.AppPref
import java.util.*

open class BaseScreen : AppCompatActivity() {
    private var networkReceiver: BroadcastReceiver? = null
    lateinit var appPref : AppPref
    private var mainScreen: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appPref = App.getInstance().appPref

        listenNetworkChange()
    }

    fun setMainScreenView(mainView : View) {
        mainScreen = mainView
    }

    fun isConnectedToNetwork(): Boolean {
        val manager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val networkInfo = (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        return (networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected
                && manager.isWifiEnabled && networkInfo.typeName == "WIFI")
    }

    private fun listenNetworkChange() {
        val filter = IntentFilter()
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED")
        filter.addAction("android.net.wifi.STATE_CHANGE")
        networkReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                mainScreen?.setIpAddress(getIPAddress())
            }
        }
        super.registerReceiver(networkReceiver, filter)
    }

    fun getIPAddress(): String {
        val manager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress = manager.connectionInfo.ipAddress
        val formattedIp = String.format(Locale.getDefault(), "%d.%d.%d.%d", ipAddress and 0xff, ipAddress shr 8 and 0xff, ipAddress shr 16 and 0xff, ipAddress shr 24 and 0xff)
        return if (formattedIp == "0.0.0.0") {
            "Server Not Started Yet"
        } else {
            "https://$formattedIp:8080"
        }
    }

    fun isServiceRunning(): Boolean {
        var serviceRunning = false
        val am = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val l = am.getRunningServices(50)
        for (runningServiceInfo in l) {
            if (runningServiceInfo.service.className == "com.biotel.iot.services.WebServerService") {
                if (runningServiceInfo.foreground) {
                    serviceRunning = true
                }
            }
        }
        return serviceRunning
    }

    override fun onDestroy() {
        super.onDestroy()
        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver)
        }
    }
}