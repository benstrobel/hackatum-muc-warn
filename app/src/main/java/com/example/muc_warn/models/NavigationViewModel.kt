package com.example.muc_warn.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.muc_warn.busineslogic.FetchCallback
import com.example.muc_warn.busineslogic.PeerToPeerManager
import com.example.muc_warn.components.BottomBar.Screen
import com.example.muc_warn.schema.Alert

class NavigationViewModel(
    p2pManager: PeerToPeerManager
) : ViewModel() {
    val currentScreen = mutableStateOf<Screen>(Screen.Warnings)

    var isNetworkAvailable: MutableState<Boolean> = mutableStateOf(false)

    val alertList = mutableStateListOf<Alert>()
    val ptp: PeerToPeerManager

    init {
        ptp  = p2pManager
        //callback is a bit ugly but hey it's effecient :P
        class CallbackImpl : FetchCallback {
            override fun onCallback(list: List<Alert>) {
                alertList.addAll(list.reversed())
            }
        }
        val cb = CallbackImpl()
        ptp.fetchNewAlerts(cb)
    }


    /*    val alertList = mutableListOf(
            Alert(
                senderName = "Lena Müller",
                title = "Starkregenwarnung",
                description = "Es wird erwartet, dass in den nächsten Stunden Starkregen die Region um Hauptstraße 15 in Berlin erreichen wird. Mögliche Auswirkungen sind Überschwemmungen und rutschige Straßen. Bitte nehmen Sie Vorsichtsmaßnahmen und vermeiden Sie unnötige Fahrten.",
                threadLevel = 2,
                postDate = Date(),
                expireDate = Date(),
                location = Location(52.5200, 13.4050),
                locationString = "Hauptstraße 15, Berlin"
            ),
            Alert(
                senderName = "Lena Müller",
                title = "Schneesturmwarnung",
                description = "Ein intensiver Schneesturm wird voraussichtlich die Region um Bergweg 7 in München beeinflussen. Erwartet werden starke Schneefälle und schlechte Sichtverhältnisse auf den Straßen. Bitte bereiten Sie sich auf winterliche Bedingungen vor und vermeiden Sie unnötige Reisen.",
                threadLevel = 1,
                postDate = Date(),
                expireDate = Date(),
                location = Location(48.8566, 2.3522),
                locationString = "Bergweg 7, München"
            ),
            Alert(
                senderName = "Lena Müller",
                title = "Hochwasserwarnung",
                description = "Aufgrund von starken Regenfällen wird eine Hochwassergefahr für das Gebiet um Uferstraße 22 in Hamburg gemeldet. Bewohner in gefährdeten Gebieten sollten sich auf evakuieren vorbereiten und Hochwasserschutzmaßnahmen ergreifen.",
                threadLevel = 3,
                postDate = Date(),
                expireDate = Date(),
                location = Location(53.5511, 9.9937),
                locationString = "Uferstraße 22, Hamburg"
            ),
            Alert(
                senderName = "Lena Müller",
                title = "Tornado-Warnung",
                description = "Eine Tornado-Warnung wurde für die Gegend um Waldweg 10 in Frankfurt ausgegeben. Bewohner werden aufgefordert, sofort Schutz in einem stabilen Gebäude zu suchen und sich von Fenstern fernzuhalten. Bitte bleiben Sie in Sicherheit, bis die Warnung aufgehoben wird.",
                threadLevel = 4,
                postDate = Date(),
                expireDate = Date(),
                location = Location(50.1109, 8.6821),
                locationString = "Waldweg 10, Frankfurt"
            ),
            Alert(
                senderName = "Lena Müller",
                title = "Hitzewarnung",
                description = "Extreme Hitze wird erwartet, die das Gebiet um Sonnenallee 5 in Stuttgart betreffen wird. Es wird empfohlen, sich vor direkter Sonneneinstrahlung zu schützen, ausreichend Wasser zu trinken und kühlere Orte aufzusuchen, um Hitzschläge zu vermeiden.",
                threadLevel = 2,
                postDate = Date(),
                expireDate = Date(),
                location = Location(48.7758, 9.1829),
                locationString = "Sonnenallee 5, Stuttgart"
            )
        )*/
}