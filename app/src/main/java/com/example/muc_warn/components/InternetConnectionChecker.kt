package com.example.muc_warn.components

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import com.example.muc_warn.models.NavigationViewModel
import kotlinx.coroutines.delay

@Composable
fun InternetConnectionChecker(isNetworkAvailable: MutableState<Boolean>, topPaddingValues: PaddingValues, viewModel: NavigationViewModel) {
    val context = LocalContext.current
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

// Nun kannst du auf die Liste alertList zugreifen und Operationen wie hinzufügen, entfernen oder ändern durchführen.


    // Observe network changes
    LaunchedEffect(connectivityManager) {
        while (true) {
            isNetworkAvailable.value = checkInternetConnection(connectivityManager)
            delay(1000) // Check every second (adjust as needed)
        }
    }
}

fun checkInternetConnection(connectivityManager: ConnectivityManager): Boolean {
    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)
    return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}