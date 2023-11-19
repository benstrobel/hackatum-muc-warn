package com.example.muc_warn.busineslogic.wifidirect

import android.annotation.SuppressLint
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import android.util.Log
import android.net.wifi.p2p.WifiP2pManager.Channel
import android.os.Handler

class WiFiDirectServiceDiscovery(
    private val manager: WifiP2pManager,
    private val channel: Channel,
    private val handler: Handler,
    private val onServiceDiscovered: (value: WiFiP2pService) -> Unit) {
    companion object {
        val TAG = "WifiDirectServiceDiscovery"
        val INSTANCE_NAME = "PEER_GUARD"
        val SERVICE_NAME = "peerguard"
        val SERVICE_TYPE = "_presence._tcp"
        val VERSION = "1.0.0"
        val timeout: Long = 5000
    }

    val runnable = WiFiDirectServiceDiscoveryRunnable(handler, {discoverService()}, timeout)
    init {
        startPeerGuardService()
    }

    fun start(){
        startPeerGuardService()
        onResume()
    }

    fun onResume() {
        handler.postDelayed(runnable, timeout)
    }

    fun onPause() {
        handler.removeCallbacks(runnable)
    }

    @SuppressLint("MissingPermission")
    private fun startPeerGuardService() {
        val records: Map<String, String> = mapOf(
            "servicename" to SERVICE_NAME,
            "version" to VERSION
        )
        val serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(INSTANCE_NAME,SERVICE_TYPE, records)

        manager.addLocalService(channel, serviceInfo, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                // Command successful! Code isn't necessarily needed here,
                // Unless you want to update the UI or add logging statements.
                println("Registered local service")
            }

            override fun onFailure(arg0: Int) {
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                println("Failed to register local service")
            }
        })

        val servListener =
            WifiP2pManager.DnsSdServiceResponseListener { instanceName, registrationType, srcDevice ->
                Log.d(
                    TAG,
                    "In Serv Listener " + instanceName + " " + registrationType + " " + srcDevice.deviceName
                )
                if (instanceName.equals(INSTANCE_NAME)) {
                    Log.d(TAG, "Is our service")
                    onServiceDiscovered(WiFiP2pService(srcDevice, instanceName, registrationType))
                }
            }

        val txtListener =
            WifiP2pManager.DnsSdTxtRecordListener { fullDomainName, txtRecordMap, srcDevice ->
                Log.d(
                    TAG,
                    "Found Service " + fullDomainName + " " + txtRecordMap["servicename"] + " " + txtRecordMap["version"] + " " + srcDevice.deviceName
                )
            }

        manager.setDnsSdResponseListeners(channel, servListener, txtListener)
        discoverService()
    }

    @SuppressLint("MissingPermission")
    private fun discoverService() {
        val serviceRequest: WifiP2pDnsSdServiceRequest = WifiP2pDnsSdServiceRequest.newInstance()
        manager.addServiceRequest(channel, serviceRequest,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d(TAG,"Added service discovery request")
                }

                override fun onFailure(arg0: Int) {
                    Log.d(TAG,"Failed adding service discovery request")
                }
            }
        )
        manager.discoverServices(
            channel,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    println("Discovering services...")
                    // Success!
                }

                override fun onFailure(code: Int) {
                    // Command failed. Check for P2P_UNSUPPORTED, ERROR, or BUSY
                    Log.e(TAG, "Failure: " + code)
                    when (code) {
                        WifiP2pManager.P2P_UNSUPPORTED -> {
                            Log.d(TAG, "Wi-Fi Direct isn't supported on this device.")
                        }
                    }
                }
            }
        )
    }
}