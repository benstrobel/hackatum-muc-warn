package com.muc.warn

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.ActionListener
import android.os.Bundle
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
import androidx.core.app.ActivityCompat
import com.muc.warn.ui.theme.MucWarnTheme

private var TAG = "MainActivity";

class MainActivity : ComponentActivity() {
    companion object {
        val TAG = "MainActivity"
        val INITIAL_PERMISSION_CHECK_ID = 1
        val REQUIRED_PERMISSION_ARRAY = if(android.os.Build.VERSION.SDK_INT < 33) arrayOf(Manifest.permission.ACCESS_FINE_LOCATION) else arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.NEARBY_WIFI_DEVICES)
    }

    private lateinit var wiFiDirectReceiver: WiFiDirectBroadcastReceiver
    private lateinit var intentFilter: IntentFilter
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var manager: WifiP2pManager

    private var isWifiP2pEnabled = false

    fun setIsWifiP2pEnabled(isWifiP2pEnabled: Boolean) {
        this.isWifiP2pEnabled = isWifiP2pEnabled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MucWarnTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Erdnuss", this)
                }
            }
        }
        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, null)

        wiFiDirectReceiver = WiFiDirectBroadcastReceiver(manager, channel, this)
        intentFilter = IntentFilter()
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(REQUIRED_PERMISSION_ARRAY, INITIAL_PERMISSION_CHECK_ID)
        }
    }

    // TODO Replace, see deprecation msg of super func
    @Deprecated(message = "TODO Replace")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == INITIAL_PERMISSION_CHECK_ID) {
            if(!grantResults.toList().toTypedArray().contentEquals(Array(REQUIRED_PERMISSION_ARRAY.size){0})) {
                requestPermissions(REQUIRED_PERMISSION_ARRAY, INITIAL_PERMISSION_CHECK_ID)
                Log.d(TAG, "Reqrequesting permissions because not all of them were granted")
            }
        }
    }

    // Done
    fun discover() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "Missing permission before enabling discovery")
        }
        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "Discovery init successfully")
            }

            override fun onFailure(reasonCode: Int) {
                Log.e(TAG, "Discovery init failed reason: " + reasonCode)
            }
        })
    }

    @SuppressLint("MissingPermission")
    fun connect(config: WifiP2pConfig) {
        manager.connect(channel, config, object: WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "Connection to peer" + config.deviceAddress + " succeeded")
            }

            override fun onFailure(reason: Int) {
                Log.e(TAG, "Failed to connect to peer " + config.deviceAddress)
            }

        })
    }

    override fun onResume() {
        super.onResume()
        try {
            registerReceiver(wiFiDirectReceiver, intentFilter)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
        }
    }

    /*override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(wiFiDirectReceiver)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
        }
    }*/

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(wiFiDirectReceiver)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
        }
    }
}

@Composable
fun Greeting(name: String, activity: MainActivity?, modifier: Modifier = Modifier) {
    Text(
            text = "Servus $name!",
            modifier = modifier
    )
    Column {
        Button(onClick = {activity?.discover()}) {
            Text("Start Discovery")
        }
        Button(onClick = { Log.d(TAG, "Pressed")}) {
            Text("Press Button")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MucWarnTheme {
        Greeting("Android", null)
    }
}