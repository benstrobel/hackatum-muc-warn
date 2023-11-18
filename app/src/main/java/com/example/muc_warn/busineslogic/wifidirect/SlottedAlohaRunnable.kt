package com.example.muc_warn.busineslogic.wifidirect

import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.os.Handler
import android.util.Log

class SlottedAlohaRunnable(val wiFiDirectManager: WiFiDirectManager, val handler: Handler): Runnable {
    override fun run() {
        wiFiDirectManager.connectedPeersMap.values.forEach { peer ->
            Log.d(WiFiDirectBroadcastReceiver.TAG, "Slotted Aloha found Address: " + peer.macAddress)
            wiFiDirectManager.connect(WifiP2pConfig().apply {
                deviceAddress= peer.macAddress
                wps.setup = WpsInfo.PBC
            })
        }
        handler.postDelayed(this, (Math.random()*3000+2000).toLong()) // waiting between 2 and 5 secs
    }
}