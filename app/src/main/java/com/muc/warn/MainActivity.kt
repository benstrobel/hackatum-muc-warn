package com.muc.warn

import android.Manifest
import android.annotation.SuppressLint
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.muc.warn.ui.theme.MucWarnTheme


private var TAG = "MainActivity";

class MainActivity : ComponentActivity() {
    companion object {
        val TAG = "MainActivity"
     }

    private lateinit var wiFiDirectManager: WiFiDirectManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MucWarnTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Erdnuss", this, wiFiDirectManager)
                }
            }
        }
        wiFiDirectManager = WiFiDirectManager(this)
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

@Composable
fun Greeting(name: String, activity: MainActivity?, wiFiDirectManager: WiFiDirectManager?, modifier: Modifier = Modifier) {
    Text(
            text = "Servus $name!",
            modifier = modifier
    )
    Column {
        Button(onClick = {wiFiDirectManager?.discover()}) {
            Text("Start Discovery")
        }
        Button(onClick = { Log.d(TAG, "Pressed")}) {
            Text("Press Button")
        }
        Button(onClick = { wiFiDirectManager?.startServer()}) {
            Text("Host")
        }
        Button(onClick = { wiFiDirectManager?.connectSocket()}) {
            Text("Connect")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MucWarnTheme {
        Greeting("Android", null, null)
    }
}