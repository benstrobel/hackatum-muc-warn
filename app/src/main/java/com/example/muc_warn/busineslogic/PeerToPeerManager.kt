package com.example.muc_warn.busineslogic

import android.os.Handler
import com.example.muc_warn.busineslogic.wifidirect.NewConnectedPeer
import com.example.muc_warn.schema.Alert
import com.example.muc_warn.schema.Location
import java.util.Date

interface FetchCallback{
    fun onCallback(arr: List<Alert>)
}

class PeerToPeerManager() {
    private val newAlerts = ArrayList<Alert>()
    private var callback: FetchCallback? = null

    init {


        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                newAlerts.add(
                    Alert(
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
        handler.post(runnable)
    }

    fun newConnectionPeerConsumer(value: NewConnectedPeer){


    }

    fun fetchNewAlerts(callback: FetchCallback){
        this.callback = callback
    }
}