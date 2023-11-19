package com.example.muc_warn

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.muc_warn.busineslogic.PeerToPeerManager
import com.example.muc_warn.busineslogic.wifidirect.NewConnectedPeer
import com.example.muc_warn.busineslogic.wifidirect.WiFiDirectManager
import com.example.muc_warn.components.BottomBar.BottomNavBar
import com.example.muc_warn.components.BottomBar.Screen
import com.example.muc_warn.components.IndicatorTopBar
import com.example.muc_warn.components.InternetConnectionChecker
import com.example.muc_warn.components.getColorBasedOnNumber
import com.example.muc_warn.models.NavigationViewModel
import com.example.muc_warn.ui.theme.MucWarnTheme
import com.example.muc_warn.views.CreateAlertView
import com.example.muc_warn.views.InfoView
import com.example.muc_warn.views.MainView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng

class MainActivity : ComponentActivity() {

    private lateinit var wiFiDirectManager: WiFiDirectManager
    private lateinit var p2pManager: PeerToPeerManager
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback
    var locationRequired: Boolean = false

    private val permissions = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
    )
    override fun onResume() {
        super.onResume()
        if(locationRequired) {
            startLocationUpdates()
        }
        wiFiDirectManager.onResume()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        locationCallback?.let {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 100
            )
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(3000)
                .setMaxUpdateAgeMillis(100)
                .build()

            fusedLocationProviderClient?.requestLocationUpdates(
                locationRequest,
                it,
                Looper.getMainLooper()
            )
        }
    }

    override fun onPause() {
        super.onPause()
        locationCallback?.let{
            fusedLocationProviderClient?.removeLocationUpdates(it)
        }
        wiFiDirectManager.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        p2pManager = PeerToPeerManager()
        wiFiDirectManager = WiFiDirectManager(this) { newPeer: NewConnectedPeer -> p2pManager.newConnectionPeerConsumer(newPeer) }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            var currentLocation by remember {
                mutableStateOf(LatLng(0.toDouble(),0.toDouble()))
            }

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    for(location in p0.locations) {
                        currentLocation = LatLng(location.latitude, location.longitude)
                    }
                }
            }

            MucWarnTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val navigationViewModel = NavigationViewModel(p2pManager)

                    NavHost(navController = navController, startDestination = "warnings" ) {
                        composable("warnings") {
                            MainView(navController = navController, viewModel = navigationViewModel)
                        }
                        composable("info") {
                            InfoView(navController = navController, viewModel = navigationViewModel)
                        }
                        composable("map") {
                            LocationScreen(this@MainActivity,currentLocation, navigationViewModel, navController)
                        }
                        composable("create") {
                            CreateAlertView(navController = navController, viewModel = navigationViewModel, p2pManager)
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
    @Composable
    private fun LocationScreen(context: Context, currentLocation: LatLng, viewModel: NavigationViewModel, navController: NavHostController) {
        val launcheMultiplePermissions = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                permissionMaps ->
            val areGranted = permissionMaps.values.reduce{acc, next -> acc && next}
            if (areGranted) {
                locationRequired = true
                startLocationUpdates()
                Toast.makeText(context, "Permissions", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

        Scaffold(
            topBar = {
                IndicatorTopBar(isNetworkAvailable = viewModel.isNetworkAvailable, title = "PeerGuard")
            },
            bottomBar = {
                BottomNavBar(
                    navController = navController,
                    currentScreenId = viewModel.currentScreen.value.id,
                    onItemSelected = {viewModel.currentScreen.value = it},
                )
            }
        ) { innerPadding ->
            InternetConnectionChecker(viewModel.isNetworkAvailable, innerPadding, viewModel = viewModel)
            // Use a LazyColumn for better performance

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color(0xFFEEF1EE),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(1.dp, Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .border(
                                2.dp, MaterialTheme.colorScheme.primary, shape = CircleShape
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center) {
                        Icon(
                            Icons.Outlined.LocationOn, // Replace with your icon resource
                            contentDescription = null, // Provide a content description for accessibility
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text("Your Location: ", color = Color.Black, fontWeight = FontWeight.Bold)
                    Text("Latitude: ${currentLocation.latitude}", color = Color.Black)
                    Text("Longitude: ${currentLocation.latitude}", color = Color.Black)

                    Button(onClick={
                        if(permissions.all {
                                ContextCompat.checkSelfPermission(context,it) == PackageManager.PERMISSION_GRANTED
                            }) {
                            startLocationUpdates()
                        } else {
                            launcheMultiplePermissions.launch(permissions)
                        }
                    }) {
                        Text(text="Get your location")
                    }
                }

        }
    }

    @Deprecated(message = "Replace, see deprecation msg of super func")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        wiFiDirectManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}