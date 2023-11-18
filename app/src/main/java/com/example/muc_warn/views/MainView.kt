package com.example.muc_warn.views

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.muc_warn.components.IndicatorTopBar
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainView() {
    var isNetworkAvailable: MutableState<Boolean> = mutableStateOf(false);
    Scaffold(
        topBar = {
            IndicatorTopBar(isNetworkAvailable, "Status")
        },
    ) { innerPadding ->
        InternetConnectionChecker(isNetworkAvailable, innerPadding)
    }
}



@Composable
fun InternetConnectionChecker(isNetworkAvailable: MutableState<Boolean>, topPaddingValues: PaddingValues) {
    val context = LocalContext.current
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // Observe network changes
    LaunchedEffect(connectivityManager) {
        while (true) {
            isNetworkAvailable.value = checkInternetConnection(connectivityManager)
            delay(1000) // Check every second (adjust as needed)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp).padding(topPaddingValues),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(isNetworkAvailable.value) {
            OfflineView()
        } else{
            OfflineView()
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
