package com.muc.warn

import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.os.Handler
import android.os.Looper
import android.util.Log

class SlottedAlohaRunnable(val wiFiDirectManager: WiFiDirectManager, val handler: Handler): Runnable {
    override fun run() {
        wiFiDirectManager.peers.forEach { peer ->
            // Comment this block out on one device and leave it in on the other one for now
            if(peer.status == WifiP2pDevice.AVAILABLE) {
                Log.d(WiFiDirectBroadcastReceiver.TAG, "Status: " + peer.status + " Address: " + peer.deviceAddress)
                wiFiDirectManager.connect(WifiP2pConfig().apply {
                    deviceAddress= peer.deviceAddress
                    wps.setup = WpsInfo.PBC
                })
            }
        }
        handler.postDelayed(this, (Math.random()*3000+2000).toLong()) // waiting between 2 and 5 secs
    }
}