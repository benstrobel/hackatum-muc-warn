package com.example.muc_warn

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.muc_warn.components.BottomBar.Screen
import com.example.muc_warn.components.BottomBar.hasPermission
import com.example.muc_warn.models.NavigationViewModel
import com.example.muc_warn.ui.theme.MucWarnTheme
import com.example.muc_warn.views.CreateAlertView
import com.example.muc_warn.views.InfoView
import com.example.muc_warn.views.MainView
import com.example.muc_warn.views.MapView
import com.example.muc_warn.views.SettingView
import com.example.muc_warn.views.getCertificate
import com.example.muc_warn.views.validateCertificate
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class MainActivity : ComponentActivity() {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback
    var locationRequired: Boolean = false

    override fun onResume() {
        super.onResume()
        if(locationRequired) {
            startLocationUpdated()
        }
    }

    override fun onPause() {
        super.onPause()
        locationCallback?.let{
            fusedLocationProviderClient?.removeLocationUpdates(it)
        }
    }

    private fun startLocationUpdated() {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            var currentLocation by remember {
                mutableStateOf(LatLng(0.toDouble(),0.toDouble()))
            }

            MucWarnTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val navigationViewModel = NavigationViewModel()
                    val context: Context = LocalContext.current

                    if(validateCertificate(getCertificate(context = context))) {
                        navigationViewModel.mutableListItems = Screen.Items.listValidated.toMutableList()
                    }

                    NavHost(navController = navController, startDestination = "warnings" ) {
                        composable("warnings") {
                            MainView(navController = navController, viewModel = navigationViewModel)
                        }
                        composable("info") {
                            InfoView(navController = navController, viewModel = navigationViewModel)
                        }
                        composable("map") {
                            MapView(navController = navController, viewModel = navigationViewModel)
                        }
                        composable("create") {
                            CreateAlertView(navController = navController, viewModel = navigationViewModel)
                        }
                        composable("settings") {
                            SettingView(navController = navController, viewModel = navigationViewModel)
                        }
                    }
                }
            }
        }
    }
}