package com.example.muc_warn.busineslogic

import com.example.muc_warn.busineslogic.wifidirect.NewConnectedPeer
import com.example.muc_warn.schema.Alert
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader


interface FetchCallback {
    fun onCallback(arr: List<Alert>)
}

class PeerToPeerManager() {
    private val newAlerts = ArrayList<Alert>()
    private val alertsToShare = ArrayList<Alert>()

    private var callback: FetchCallback? = null

    fun addToAlertsToShare(alert: Alert) {
        alertsToShare.add(alert)
        newAlerts.add(alert)
        callback?.onCallback(newAlerts)
        newAlerts.clear()
    }

    fun newConnectionPeerConsumer(value: NewConnectedPeer) {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setDateFormat("yyyy-MM-dd")
        val gson = gsonBuilder.create()


        // write --------------------------------------

        println("Pre read " + value.macAddress)
        val s = StringBuilder()
        s.append(gson.toJson(alertsToShare))
        val msg = s.toString() + "\n"
        val barray = msg.toByteArray(Charsets.UTF_8)
        value.outputStream.write(barray)
        value.outputStream.flush()
        println("just flushed")

        // read --------------------------------------

        val inReader = BufferedReader(InputStreamReader(value.inputStream))
        val line: String? = inReader.readLine()
        if (line != null) {
            try {
                val sType = object : TypeToken<List<Alert>>() {}.type
                val alerts: List<Alert> = gson.fromJson(line.toString(), sType)
                println("received123: $alerts")
                for (alert in alerts) {
                    //only add alerts with new IDs
                    if (alertsToShare.find { a -> a.id == alert.id } == null) {
                        alertsToShare.add(alert)
                        newAlerts.add(alert)
                        callback?.onCallback(newAlerts)
                        newAlerts.clear()
                    }
                }
            } catch (e: Exception) {
                println("Error while parsing JSON: $e")
            }
        }
        value.closePeer()
        println("After")
    }

    fun fetchNewAlerts(callback: FetchCallback) {
        this.callback = callback
    }
}