package com.muc.warn

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class NewConnectedPeer(val macAddress: String, val inputStream: InputStream, val outputStream: OutputStream, val closePeer: () -> Unit)

class WiFiConnectedPeer(val macAddress: String, val clientSocket: Socket?, val serverSocket: ServerSocket?)

interface CloseNewConnectedPeer {
    fun close()
}

interface OnNewConnectedPeerListener {
    fun onNewPeer(value: NewConnectedPeer)
}

class WiFiDirectManager(val activity: MainActivity, val onNewConnectedPeerListener: OnNewConnectedPeerListener?) {
    companion object {
        val TAG = "WiFiDirectManager"
        val INITIAL_PERMISSION_CHECK_ID = 1
        val REQUIRED_PERMISSION_ARRAY = if(android.os.Build.VERSION.SDK_INT < 33) arrayOf(Manifest.permission.ACCESS_FINE_LOCATION) else arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.NEARBY_WIFI_DEVICES)
    }

    private var wiFiDirectReceiver: WiFiDirectBroadcastReceiver
    private var intentFilter: IntentFilter
    private var channel: WifiP2pManager.Channel
    private var handler: Handler
    private var slottedAlohaRunnable: SlottedAlohaRunnable

    private var manager: WifiP2pManager = activity.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    private var discoveryMode = false
    var isWifiP2pEnabled = false
    var groupOwnerAddress: String? = ""
    var connectedPeersMap: MutableMap<String, WiFiConnectedPeer> = mutableMapOf()

    init {
        channel = manager.initialize(activity, activity.mainLooper, null)
        handler = Handler(Looper.getMainLooper())
        slottedAlohaRunnable = SlottedAlohaRunnable(this, handler)

        wiFiDirectReceiver = WiFiDirectBroadcastReceiver(manager, channel, this)
        intentFilter = IntentFilter()
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
    }

    val discoverResultCallback = object: WifiP2pManager.ActionListener {
        override fun onSuccess() {
            Log.d(TAG, "Discovery init successfully")
        }

        override fun onFailure(reasonCode: Int) {
            Log.e(TAG, "Discovery init failed reason: " + reasonCode)
        }
    }

    public fun onNewPotentialPeer(macAddress: String, isHost: Boolean) {
        if(isHost) {
            connectAsServer(macAddress)
        } else {
            connectAsClient(macAddress)
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == INITIAL_PERMISSION_CHECK_ID) {
            if(!grantResults.toList().toTypedArray().contentEquals(Array(REQUIRED_PERMISSION_ARRAY.size){0})) {
                activity.requestPermissions(
                    REQUIRED_PERMISSION_ARRAY,
                    INITIAL_PERMISSION_CHECK_ID
                )
                Log.d(TAG, "Reqrequesting permissions because not all of them were granted")
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun connect(config: WifiP2pConfig) {
        manager.connect(channel, config, object: WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "Connection to peer" + config.deviceAddress + " succeeded")
            }

            override fun onFailure(reason: Int) {
                Log.e(TAG, "Failed to connect to peer " + config.deviceAddress + " Reason: " + reason)
            }

        })
    }

    fun discover() {
        discoveryMode = true
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "Missing permission before enabling discovery")
        }
        Log.d(TAG, "Entering discovery mode...")
        manager.discoverPeers(channel, discoverResultCallback)
    }

    private fun startSlottedAlohaThread() {
        println("Starting test")
        handler.postDelayed(slottedAlohaRunnable, 3000)
    }

    fun closePeerConnection(macAddress: String) {
        val peer = connectedPeersMap[macAddress] ?: return
        peer.clientSocket?.close()
        peer.serverSocket?.close()
        connectedPeersMap.remove(macAddress)
    }

    fun connectAsServer(macAddress: String) {
        println("start server")
        val thread = Thread(
            Runnable {
                val serverSocket = ServerSocket(8888)
                val client = serverSocket.accept()
                connectedPeersMap.put(macAddress, WiFiConnectedPeer(macAddress, client, serverSocket))
                onNewConnectedPeerListener?.onNewPeer(NewConnectedPeer(macAddress, client.getInputStream(), client.getOutputStream()) {
                    closePeerConnection(macAddress)
                })
            }
        )
        thread.start()
    }

    fun connectAsClient(macAddress: String) {
        println("connect socket")
        val thread = Thread(
            Runnable {
                val socket = Socket()
                socket.bind(null)
                socket.connect((InetSocketAddress(groupOwnerAddress, 8888)), 500)
                connectedPeersMap.put(macAddress, WiFiConnectedPeer(macAddress, socket, null))
                onNewConnectedPeerListener?.onNewPeer(NewConnectedPeer(macAddress, socket.getInputStream(), socket.getOutputStream()) {
                    closePeerConnection(macAddress)
                })
            }
        )
        thread.start()
    }

    fun onResume() {
        try {
            wiFiDirectReceiver = WiFiDirectBroadcastReceiver(manager, channel, this)
            activity.registerReceiver(wiFiDirectReceiver, intentFilter)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
        }
        if(discoveryMode) {
            discover()
        }
        startSlottedAlohaThread()
    }

    fun onPause() {
        try {
            activity.unregisterReceiver(wiFiDirectReceiver)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
        }
        manager.stopPeerDiscovery(channel, discoverResultCallback)
        handler.removeCallbacks(slottedAlohaRunnable)
    }
}