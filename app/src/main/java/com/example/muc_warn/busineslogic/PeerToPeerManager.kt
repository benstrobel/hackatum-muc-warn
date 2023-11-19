package com.example.muc_warn.busineslogic

import com.example.muc_warn.busineslogic.wifidirect.NewConnectedPeer
import com.example.muc_warn.schema.Alert
import com.example.muc_warn.schema.Location
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Date


interface FetchCallback {
    fun onCallback(arr: List<Alert>)
}

class PeerToPeerManager() {
    private val newAlerts = ArrayList<Alert>()
    private val alertsToShare = ArrayList<Alert>()

    private var callback: FetchCallback? = null

    init {
        alertsToShare.add(
            Alert(
                id = 1,
                senderName = "Lasser Tobias",
                title = "Starkregenwarnung",
                description = "Es wird erwartet, dass in den nächsten Stunden Starkregen die Region um Hauptstraße 15 in Berlin erreichen wird. Mögliche Auswirkungen sind Überschwemmungen und rutschige Straßen. Bitte nehmen Sie Vorsichtsmaßnahmen und vermeiden Sie unnötige Fahrten.",
                threadLevel = 2,
                postDate = Date(),
                expireDate = Date(),
                location = Location(52.5200, 13.4050),
                locationString = "Hauptstraße 15, Berlin"
            )
        )
        /*   val handler = Handler()
           val runnable = object : Runnable {
               override fun run() {
                   newAlerts.add(
                       Alert(
                           id = 1,
                           senderName = "Lana Maler",
                           title = "Starkregenwarnung",
                           description = "Es wird erwartet, dass in den nächsten Stunden Starkregen die Region um Hauptstraße 15 in Berlin erreichen wird. Mögliche Auswirkungen sind Überschwemmungen und rutschige Straßen. Bitte nehmen Sie Vorsichtsmaßnahmen und vermeiden Sie unnötige Fahrten.",
                           threadLevel = 2,
                           postDate = Date(),
                           expireDate = Date(),
                           location = Location(52.5200, 13.4050),
                           locationString = "Hauptstraße 15, Berlin"
                       )
                   )
                   callback?.onCallback(newAlerts)
                   newAlerts.clear()
                   // println("I simulate P2P and I add new Alert every 3 seconds")
                   handler.postDelayed(this, 3000)
               }
           }
           handler.post(runnable)*/
    }

    fun newConnectionPeerConsumer(value: NewConnectedPeer) {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setDateFormat("yyyy-MM-dd")
        val gson = gsonBuilder.create()

        println("In newConnectionPeerConsumer")

        // write --------------------------------------

        println("Pre read " + value.macAddress)
        val s = StringBuilder()
        for (alert in alertsToShare) {
            s.append(gson.toJson(alertsToShare))
        }

        val msg = s.toString() + "\n"
        val barray = msg.toByteArray(Charsets.UTF_8)
        value.outputStream.write(barray)
        value.outputStream.flush()
        println("just flushed")

        // read --------------------------------------

        val inReader = BufferedReader(InputStreamReader(value.inputStream))
        val line: String? = inReader.readLine()
        if (line != null) {
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
        }

        inReader.close()
        value.outputStream.close()
        println("After")
    }

    fun fetchNewAlerts(callback: FetchCallback) {
        this.callback = callback
    }
}