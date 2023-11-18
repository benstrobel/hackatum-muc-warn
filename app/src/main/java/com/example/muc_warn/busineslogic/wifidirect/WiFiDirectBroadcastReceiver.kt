package com.example.muc_warn.busineslogic.wifidirect

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log

class WiFiDirectBroadcastReceiver (private val manager: WifiP2pManager, private val channel: WifiP2pManager.Channel, private val wiFiDirectManager: WiFiDirectManager): BroadcastReceiver() {
    companion object {
        val TAG = "WiFiDirectBroadcastReceiver"
    }
    //val peers = mutableListOf<WifiP2pDevice>()
    var discoveredAvailablePeersMap: MutableMap<String, WifiP2pDevice> = mutableMapOf()
    
    private val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val discoveredPeers = peerList.deviceList
        val newPeerMap = refreshedListToMap(discoveredPeers)
        //if (discoveredAvailablePeersMap !== newPeerMap) {
            discoveredAvailablePeersMap.clear()
            discoveredAvailablePeersMap = newPeerMap
            val invitedPeers = discoveredPeers.filter { x -> x.status == WifiP2pDevice.INVITED }.size
            val availablePeers = discoveredPeers.filter { x -> x.status == WifiP2pDevice.AVAILABLE }.size
            val connectedPeers = discoveredPeers.filter { x -> x.status == WifiP2pDevice.CONNECTED }.size
            Log.d(TAG, "Updated peerlist size: " + discoveredPeers.size + " available: " + availablePeers + " invitedPeers: " + invitedPeers + " connectedPeers: " + connectedPeers)

            if (discoveredAvailablePeersMap.isEmpty()) {
                Log.d(TAG, "No devices found")
                return@PeerListListener
            }
        //}

        if (discoveredAvailablePeersMap.isEmpty()) {
            Log.d(TAG, "No devices found")
            return@PeerListListener
        }
    }

    private val connectionListener = WifiP2pManager.ConnectionInfoListener { info ->
        Log.d(TAG, "In ConnectionInfoListener")
        val groupOwnerAddress: String? = info.groupOwnerAddress.hostAddress

        if (info.groupFormed && info.isGroupOwner) {
            Log.d(TAG, "I am owner | Owner Address: $groupOwnerAddress")
            wiFiDirectManager.groupOwnerAddress = groupOwnerAddress
            //if(groupOwnerAddress == null ) return@ConnectionInfoListener
            wiFiDirectManager.onNewP2PConnection(null, null, true)
        } else if (info.groupFormed) {
            Log.d(TAG, "I am client | Owner Address: $groupOwnerAddress")
            if(groupOwnerAddress == null ) return@ConnectionInfoListener
            wiFiDirectManager.onNewP2PConnection(groupOwnerAddress, groupOwnerAddress, false)
        }
    }

    private fun refreshedListToMap(list: Collection<WifiP2pDevice>): MutableMap<String, WifiP2pDevice> {
        val map: MutableMap<String, WifiP2pDevice> = mutableMapOf()
        list.filter{x -> x.status == WifiP2pDevice.AVAILABLE}.forEach { peer ->
            map.put(peer.deviceAddress, peer)
        }
        return map
    }

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                Log.d(TAG, "Received WIFI_P2P_STATE_CHANGED_ACTION Event | Value " + state)
                when(state) {
                    WifiP2pManager.WIFI_P2P_STATE_ENABLED -> {
                        wiFiDirectManager.isWifiP2pEnabled = true
                    }
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                manager.requestPeers(channel, peerListListener)
                Log.d(this.javaClass.name, "Received WIFI_P2P_PEERS_CHANGED_ACTION Event")
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                val networkInfo: NetworkInfo? = intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO) as NetworkInfo?
                Log.d(this.javaClass.name, "Received WIFI_P2P_CONNECTION_CHANGED_ACTION Event | State: " + networkInfo?.state)
                manager.let { mngr ->
                    if (networkInfo?.isConnected == true) {
                        Log.d(TAG, "NetInfo is connected")
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
}