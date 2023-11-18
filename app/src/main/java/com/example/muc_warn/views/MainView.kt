package com.example.muc_warn.views

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.muc_warn.components.AlertCard
import com.example.muc_warn.components.BottomBar.BottomNavBar
import com.example.muc_warn.components.IndicatorTopBar
import com.example.muc_warn.components.WarningCard
import com.example.muc_warn.schema.Alert
import com.example.muc_warn.schema.Location
import kotlinx.coroutines.delay
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainView() {
    var isNetworkAvailable: MutableState<Boolean> = mutableStateOf(false);

    Scaffold(
        topBar = {
            IndicatorTopBar(isNetworkAvailable = isNetworkAvailable, title = "PeerGuard")
        },
        /*bottomBar = {
            BottomNavBar(navController = , currentScreenId = , onItemSelected = )
        }*/
    ) { innerPadding ->
        InternetConnectionChecker(isNetworkAvailable, innerPadding)
    }
}



@Composable
fun InternetConnectionChecker(isNetworkAvailable: MutableState<Boolean>, topPaddingValues: PaddingValues) {
    val context = LocalContext.current
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val alertList = mutableListOf(
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
    )

// Nun kannst du auf die Liste alertList zugreifen und Operationen wie hinzufügen, entfernen oder ändern durchführen.


    // Observe network changes
    LaunchedEffect(connectivityManager) {
        while (true) {
            isNetworkAvailable.value = checkInternetConnection(connectivityManager)
            delay(1000) // Check every second (adjust as needed)
        }
    }

    // Use a LazyColumn for better performance
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(topPaddingValues)
            .padding(horizontal = 16.dp)
    ) {
        if (!isNetworkAvailable.value) {
            item {
                WarningCard(
                    title = "Achtung",
                    subtitle = "Es konnte keine Verbindung zum Internet aufgebaut werden. Du befindest dich im Notfallmodus."
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        items(alertList) { alert ->
            AlertCard(alert = alert)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

fun checkInternetConnection(connectivityManager: ConnectivityManager): Boolean {
    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)
    return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}
@Preview
@Composable

fun MainViewPreview() {
    MainView()
}