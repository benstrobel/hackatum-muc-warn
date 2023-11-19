package com.example.muc_warn.busineslogic.wifidirect

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.muc_warn.MainActivity
import java.util.Timer
import java.util.TimerTask

class WiFiDirectServiceManager(val activity: MainActivity, val onNewConnectedPeerListener: (NewConnectedPeer) -> Unit) {
    companion object {
        val TAG = "WiFiDirectServiceManager"
        val INITIAL_PERMISSION_CHECK_ID = 1
        val timeout: Long = 3000
        val REQUIRED_PERMISSION_ARRAY = if(android.os.Build.VERSION.SDK_INT < 33) arrayOf(Manifest.permission.ACCESS_FINE_LOCATION) else arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.NEARBY_WIFI_DEVICES)

    }

    private val manager: WifiP2pManager = activity.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    private val channel: WifiP2pManager.Channel = manager.initialize(activity, activity.mainLooper, null)
    private val intentFilter: IntentFilter = IntentFilter()
    private val handler: Handler = Handler(Looper.getMainLooper())
    private val serviceDiscovery: WiFiDirectServiceDiscovery = WiFiDirectServiceDiscovery(manager, channel, handler) { v ->
        onServiceDiscovered(
            v
        )
    }
    private var wiFiDirectServiceDiscoveryReceiver: WiFiDirectServiceBroadcastReceiver? = null

    private val discoveredServices: MutableMap<String,WiFiP2pService> = mutableMapOf()
    private val timer = Timer()

    private val timerTask = object: TimerTask() {
        @SuppressLint("MissingPermission")
        override fun run() {
            onPause()
            if(discoveredServices.values.isEmpty()) return
            val randIndex = (0 until discoveredServices.values.size).random()
            val value = discoveredServices.values.toList()[randIndex]
            wiFiDirectServiceDiscoveryReceiver =
                WiFiDirectServiceBroadcastReceiver(manager, channel, value) {v ->
                    onNewConnectedPeerListener(NewConnectedPeer(value.device.deviceAddress,v.inputStream,v.outputStream) {
                        v.inputStream.close()
                        v.outputStream.close()
                        manager.removeGroup(channel, object: WifiP2pManager.ActionListener {
                            override fun onSuccess() {
                                Log.d(TAG, "Removed current p2p group")
                            }

                            override fun onFailure(reasonCode: Int) {
                                Log.e(TAG, "Failed to remove current p2p group: " + reasonCode)
                            }
                        })
                    })
                }
            onResume()
            manager.connect(channel, WifiP2pConfig().apply {
                deviceAddress = value.device.deviceAddress
                wps.setup = WpsInfo.PBC
            }, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d(TAG, "Connection to peer " + value.device.deviceAddress + " succeeded")
                }

                override fun onFailure(reason: Int) {
                    Log.e(
                        TAG,
                        "Failed to connect to peer " + value.device.deviceAddress + " Reason: " + reason
                    )
                }

            })
        }
    }

    init {
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activity.requestPermissions(
                REQUIRED_PERMISSION_ARRAY,
                INITIAL_PERMISSION_CHECK_ID
            )
        }
        timer.scheduleAtFixedRate(timerTask, 0, timeout)
    }

    @SuppressLint("MissingPermission")
    private fun onServiceDiscovered(value: WiFiP2pService) {
        discoveredServices[value.device.deviceAddress] = value
    }

    fun onResume() {
        serviceDiscovery.onResume()
        try {
            activity.registerReceiver(wiFiDirectServiceDiscoveryReceiver, intentFilter)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
        }
        timer.scheduleAtFixedRate(timerTask, 0, timeout)
    }

    fun onPause() {
        serviceDiscovery.onPause()
        timer.cancel()
        try {
            activity.unregisterReceiver(wiFiDirectServiceDiscoveryReceiver)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
        }
    }
}