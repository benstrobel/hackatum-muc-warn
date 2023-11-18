package com.muc.warn

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log

class WiFiDirectBroadcastReceiver (private val manager: WifiP2pManager, private val channel: WifiP2pManager.Channel, private val activity: MainActivity): BroadcastReceiver() {
    companion object {
        val TAG = "WiFiDirectBroadcastReceiver"
    }

    private val peers = mutableListOf<WifiP2pDevice>()
    private val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList
        if (refreshedPeers != peers) {
            peers.clear()
            peers.addAll(refreshedPeers)

            // If an AdapterView is backed by this data, notify it
            // of the change. For instance, if you have a ListView of
            // available peers, trigger an update.
            // TODO Do we need this? (listAdapter as WiFiPeerListAdapter).notifyDataSetChanged()
            // TODO Since we dont want to display the list it shouldnt be a problem

            // Perform any other updates needed based on the new list of
            // peers connected to the Wi-Fi P2P network.
            if (peers.isEmpty()) {
                Log.d(TAG, "No devices found")
                return@PeerListListener
            } else {
                peers.forEach { peer ->
                    if(peer.status == WifiP2pDevice.AVAILABLE) {
                        activity.connect(WifiP2pConfig().apply {
                            deviceAddress= peer.deviceAddress
                            wps.setup = WpsInfo.PBC
                        })
                    }
                }
            }
        }

        if (peers.isEmpty()) {
            Log.d(TAG, "No devices found")
            return@PeerListListener
        }
    }

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                Log.d(TAG, "Received WIFI_P2P_STATE_CHANGED_ACTION Event | Value " + state)
                when(state) {
                    WifiP2pManager.WIFI_P2P_STATE_ENABLED -> {
                        activity.setIsWifiP2pEnabled(true)
                    }
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                manager.requestPeers(channel, peerListListener)
                Log.d(this.javaClass.name, "Received WIFI_P2P_PEERS_CHANGED_ACTION Event")
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                Log.d(this.javaClass.name, "Received WIFI_P2P_CONNECTION_CHANGED_ACTION Event")
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                Log.d(this.javaClass.name, "Received WIFI_P2P_THIS_DEVICE_CHANGED_ACTION Event")
            }
        }
    }
}