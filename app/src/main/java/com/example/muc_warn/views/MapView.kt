package com.example.muc_warn.views

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.muc_warn.components.BottomBar.BottomNavBar
import com.example.muc_warn.components.IndicatorTopBar
import com.example.muc_warn.components.InternetConnectionChecker
import com.example.muc_warn.models.NavigationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MapView(navController: NavController, viewModel: NavigationViewModel) {

    Scaffold(
        topBar = {
            IndicatorTopBar(isNetworkAvailable = viewModel.isNetworkAvailable, title = "PeerGuard")
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                currentScreenId = viewModel.currentScreen.value.id,
                onItemSelected = {viewModel.currentScreen.value = it},
                viewModel = viewModel
            )
        }
    ) { innerPadding ->
        Box (modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)){
            InternetConnectionChecker(viewModel.isNetworkAvailable, innerPadding, viewModel = viewModel)
            // Use a LazyColumn for better performance
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
                ) {
                Text("Your Location", color = Color.Black)
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Get your location")
                }
            }
        }
    }
}