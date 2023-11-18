package com.example.muc_warn.busineslogic

import android.os.Handler
import com.example.muc_warn.busineslogic.wifidirect.NewConnectedPeer
import com.example.muc_warn.schema.Alert
import com.example.muc_warn.schema.Location
import java.io.BufferedReader
import java.io.InputStreamReader
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
        println("In newConnectionPeerConsumer")
        val inReader = BufferedReader(InputStreamReader(value.inputStream))
        //val outWriter = BufferedWriter(OutputStreamWriter(value.outputStream))

        val payload = "Servus"

        println("Pre read " + value.macAddress)
        if(value.macAddress.equals("server")) {
            val payloadWithTerminator = payload+"\n"
            val barray = payloadWithTerminator.toByteArray(Charsets.UTF_8)
            value.outputStream.write(barray)
            value.outputStream.flush()
        } else {
            val recevied = inReader.readLine()
            println("Received2: " + recevied)
            if(recevied.equals(payload)) {
                println("Closing connection")
                value.closePeer()
            }
        }
        println("After")
    }

    fun fetchNewAlerts(callback: FetchCallback){
        this.callback = callback
    }
}