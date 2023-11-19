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
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Timer
import java.util.TimerTask

class WiFiDirectServiceManager(val activity: MainActivity, val onNewConnectedPeerListener: (NewConnectedPeer) -> Unit) {
    companion object {
        val TAG = "WiFiDirectServiceManager"
        val INITIAL_PERMISSION_CHECK_ID = 1
        val timeout: Long = 2000
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
    private var currentService: ConnectedWiFiP2PService? = null
    private var lastSelectedTime = LocalDateTime.now()
    private val timer = Timer()

    private val timerTask = object: TimerTask() {
        @SuppressLint("MissingPermission")
        override fun run() {
            Log.d(TAG, "Running Connect Loop " + discoveredServices.size)
            if(discoveredServices.isNotEmpty() && currentService == null) {
                val value: WiFiP2pService = discoveredServices.values.first()
                discoveredServices.remove(value.device.deviceAddress)
                lastSelectedTime = LocalDateTime.now()
                Log.d(TAG, "Selected value " + value.device.deviceAddress)
                wiFiDirectServiceDiscoveryReceiver =
                    WiFiDirectServiceBroadcastReceiver(manager, channel, value) {v ->
                        currentService = v;
                        onNewConnectedPeerListener(NewConnectedPeer(value.device.deviceAddress,v.inputStream,v.outputStream) {
                            disconnect()
                        })
                    }
                try {
                    activity.registerReceiver(wiFiDirectServiceDiscoveryReceiver, intentFilter)
                } catch (e: Exception) {
                    e.message?.let { Log.e(TAG, it) }
                }
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
            } else if (ChronoUnit.SECONDS.between(lastSelectedTime, LocalDateTime.now()) > 15) {
                disconnect()
                currentService = null;
            } else if (currentService != null) {
                manager.connect(channel, WifiP2pConfig().apply {
                    deviceAddress = currentService!!.service.device.deviceAddress
                    wps.setup = WpsInfo.PBC
                }, object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        Log.d(TAG, "Connection to peer " + currentService?.service?.device?.deviceAddress + " succeeded")
                    }

                    override fun onFailure(reason: Int) {
                        Log.e(
                            TAG,
                            "Failed to connect to peer " + currentService?.service?.device?.deviceAddress  + " Reason: " + reason
                        )
                    }

                })
            }
        }
    }

    private fun disconnect() {
        if(currentService == null) return
        currentService!!.inputStream.close()
        currentService!!.outputStream.close()
        manager.removeGroup(channel, object: WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "Removed current p2p group")
            }

            override fun onFailure(reasonCode: Int) {
                Log.e(TAG, "Failed to remove current p2p group: " + reasonCode)
            }
        })
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
        serviceDiscovery.start()
    }

    @SuppressLint("MissingPermission")
    private fun onServiceDiscovered(value: WiFiP2pService) {
        if(!discoveredServices.contains(value.device.deviceAddress)) {
            Log.i(TAG, "Discovered unknown service on " + value.device.deviceAddress)
        }
        discoveredServices[value.device.deviceAddress] = value
    }

    fun onResume() {
        serviceDiscovery.onResume()
        timer.scheduleAtFixedRate(timerTask, 0, timeout)
    }

    fun onPause() {
        serviceDiscovery.onPause()
        timer.cancel()
    }
}