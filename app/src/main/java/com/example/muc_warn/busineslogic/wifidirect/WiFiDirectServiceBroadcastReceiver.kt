package com.example.muc_warn.busineslogic.wifidirect

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class WiFiDirectServiceBroadcastReceiver(
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel,
    private val service: WiFiP2pService,
    private val onServiceConnected: (value: ConnectedWiFiP2PService) -> Unit
): BroadcastReceiver() {

    companion object {
        val TAG = "WiFiDirectServiceBroadcastReceiver"
    }
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                Log.d(TAG, "Received WIFI_P2P_STATE_CHANGED_ACTION Event | Value " + state)
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                //manager.requestPeers(channel, peerListListener)
                //Log.d(this.javaClass.name, "Received WIFI_P2P_PEERS_CHANGED_ACTION Event")
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                val networkInfo: NetworkInfo? = intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO) as NetworkInfo?
                Log.d(this.javaClass.name, "Received WIFI_P2P_CONNECTION_CHANGED_ACTION Event | State: " + networkInfo?.state)
                manager.let { mngr ->
                    if (networkInfo?.isConnected == true) {
                        manager.requestConnectionInfo(channel, connectionListener)
                    }
                }
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                val device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE) as WifiP2pDevice?
                Log.d(this.javaClass.name, "Received WIFI_P2P_THIS_DEVICE_CHANGED_ACTION Event | Device: " + device?.status)
            }
        }
    }

    private val connectionListener = WifiP2pManager.ConnectionInfoListener { info ->
        val groupOwnerAddress: String? = info.groupOwnerAddress.hostAddress

        if (info.groupFormed && info.isGroupOwner) {
            Log.d(TAG, "I am owner | Owner Address: $groupOwnerAddress")
            if(groupOwnerAddress == null ) return@ConnectionInfoListener
            startSocketAsServer(service)
        } else if (info.groupFormed) {
            Log.d(TAG, "I am client | Owner Address: $groupOwnerAddress")
            if(groupOwnerAddress == null ) return@ConnectionInfoListener
            startSocketAsClient(service, groupOwnerAddress)
        }
    }

    private fun startSocketAsServer(service: WiFiP2pService) {
        println("starting server server")
        val thread = Thread(
            Runnable {
                try {
                    val serverSocket = ServerSocket(8888)
                    val client = serverSocket.accept()
                    onServiceConnected(ConnectedWiFiP2PService(service, client.getInputStream(), client.getOutputStream()))
                } catch (ex: Exception) {

                }
            }
        )
        thread.start()
    }

    private fun startSocketAsClient(service: WiFiP2pService, groupOwnerAddress: String) {
        println("connect socket")
        val thread = Thread(
            Runnable {
                try {
                    val socket = Socket()
                    socket.bind(null)
                    socket.connect((InetSocketAddress(groupOwnerAddress, 8888)), 500)
                    onServiceConnected(ConnectedWiFiP2PService(service, socket.getInputStream(), socket.getOutputStream()))
                } catch (ex: Exception) {

                }
            }
        )
        thread.start()
    }
}