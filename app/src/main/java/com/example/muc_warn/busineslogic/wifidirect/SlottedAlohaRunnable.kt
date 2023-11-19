package com.example.muc_warn.busineslogic.wifidirect

import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.os.Handler
import android.util.Log

class SlottedAlohaRunnable(val wiFiDirectManager: WiFiDirectManager, val handler: Handler): Runnable {
    companion object {
        val TAG = "SlottedAlohaRunnable"
    }

    override fun run() {
        wiFiDirectManager.getDiscoveredAvailablePeers().values.forEach { peer ->
            Log.d(TAG, "Slotted Aloha found Address: " + peer.deviceAddress)
            wiFiDirectManager.connect(WifiP2pConfig().apply {
                deviceAddress= peer.deviceAddress
                wps.setup = WpsInfo.PBC
            })
        }
        handler.postDelayed(this, (Math.random()*3000+2000).toLong()) // waiting between 2 and 5 secs
    }
}