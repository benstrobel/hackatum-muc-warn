package com.muc.warn

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

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
                    Log.d(TAG, "Status: " + peer.status + " Address: " + peer.deviceAddress)
                    // Comment this block out on one device and leave it in on the other one for now
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

    private val connectionListener = WifiP2pManager.ConnectionInfoListener { info ->
        Log.d(TAG, "In ConnectionInfoListener")
        // String from WifiP2pInfo struct
        val groupOwnerAddress: String? = info.groupOwnerAddress.hostAddress

        // After the group negotiation, we can determine the group owner
        // (server).
        if (info.groupFormed && info.isGroupOwner) {
            Log.d(TAG, "I am owner | Owner Address: $groupOwnerAddress")
            activity.groupOwnerAddress = groupOwnerAddress
            // Do whatever tasks are specific to the group owner.
            // One common case is creating a group owner thread and accepting
            // incoming connections.
        } else if (info.groupFormed) {
            Log.d(TAG, "I am client | Owner Address: $groupOwnerAddress")
            activity.groupOwnerAddress = groupOwnerAddress
            // The other device acts as the peer (client). In this case,
            // you'll want to create a peer thread that connects
            // to the group owner.
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
                val networkInfo: NetworkInfo? = intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO) as NetworkInfo?
                Log.d(this.javaClass.name, "Received WIFI_P2P_CONNECTION_CHANGED_ACTION Event | State: " + networkInfo?.state)
                manager.let { mngr ->
                    if (networkInfo?.isConnected == true) {
                        Log.d(TAG, "NetInfo is connected")
                        // We are connected with the other device, request connection
                        // info to find group owner IP

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