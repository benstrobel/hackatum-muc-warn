package com.example.muc_warn.busineslogic.wifidirect

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.Channel
import android.os.Handler
import android.util.Log
import com.example.muc_warn.MainActivity

class WiFiDirectServiceConnectorRunnable(
    val activity: MainActivity,
    val intentFilter: IntentFilter,
    val manager: WifiP2pManager,
    val channel: Channel,
    val handler: Handler,
    val onPause: () -> Unit,
    val onResume: () -> Unit,
    val getRandomService: () -> WiFiP2pService,
    val onNewConnectedPeerListener: (NewConnectedPeer) -> Unit,
    val timeout: Long
): Runnable {
    companion object {
        val TAG = "WiFiDirectServiceConnectorRunnable"
    }

    private var wiFiDirectServiceReceiver: WiFiDirectServiceBroadcastReceiver? = null

    fun onResume() {
        try {
            activity.registerReceiver(wiFiDirectServiceReceiver, intentFilter)
        } catch (e: Exception) {
            e.message?.let { Log.e(WiFiDirectServiceManager.TAG, it) }
        }
    }

    fun onPause() {

    }

    @SuppressLint("MissingPermission")
    override fun run() {
        onPause()
        val value = getRandomService()
        wiFiDirectServiceReceiver = WiFiDirectServiceBroadcastReceiver(manager, channel, value) { v ->
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
            deviceAddress= value.device.deviceAddress
            wps.setup = WpsInfo.PBC
        }, object: WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "Connection to peer " + value.device.deviceAddress + " succeeded")
            }

            override fun onFailure(reason: Int) {
                Log.e(TAG, "Failed to connect to peer " + value.device.deviceAddress + " Reason: " + reason)
            }

        })
        handler.postDelayed(this, timeout)
    }
}