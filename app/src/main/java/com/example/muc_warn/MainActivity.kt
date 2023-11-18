package com.example.muc_warn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.muc_warn.busineslogic.PeerToPeerManager
import com.example.muc_warn.busineslogic.wifidirect.NewConnectedPeer
import com.example.muc_warn.busineslogic.wifidirect.WiFiDirectManager
import com.example.muc_warn.models.NavigationViewModel
import com.example.muc_warn.ui.theme.MucWarnTheme
import com.example.muc_warn.views.InfoView
import com.example.muc_warn.views.MainView
import com.example.muc_warn.views.SettingView

class MainActivity : ComponentActivity() {

    private lateinit var wiFiDirectManager: WiFiDirectManager
    private lateinit var p2pManager: PeerToPeerManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        p2pManager = PeerToPeerManager()
        wiFiDirectManager = WiFiDirectManager(this) { newPeer: NewConnectedPeer -> p2pManager.newConnectionPeerConsumer(newPeer) }
        setContent {
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
                        composable("settings") {
                            SettingView(navController = navController, viewModel = navigationViewModel)
                        }
                    }
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

    override fun onResume() {
        super.onResume()
        wiFiDirectManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        wiFiDirectManager.onPause()
    }
}