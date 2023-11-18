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
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

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
    private var discoveryMode = false
    public var groupOwnerAddress: String? = ""

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

    val discoverResultCallback = object: WifiP2pManager.ActionListener {
        override fun onSuccess() {
            Log.d(TAG, "Discovery init successfully")
        }

        override fun onFailure(reasonCode: Int) {
            Log.e(TAG, "Discovery init failed reason: " + reasonCode)
        }
    }

    // Done
    fun discover() {
        discoveryMode = true
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
        Log.d(TAG, "Discovering...")
        manager.discoverPeers(channel, discoverResultCallback)
    }

    @SuppressLint("MissingPermission")
    fun connect(config: WifiP2pConfig) {
        manager.connect(channel, config, object: WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "Connection to peer" + config.deviceAddress + " succeeded")
            }

            override fun onFailure(reason: Int) {
                Log.e(TAG, "Failed to connect to peer " + config.deviceAddress + " Reason: " + reason)
            }

        })
    }

    override fun onResume() {
        super.onResume()
        try {
            wiFiDirectReceiver = WiFiDirectBroadcastReceiver(manager, channel, this)
            registerReceiver(wiFiDirectReceiver, intentFilter)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
        }
        if(discoveryMode) {
            discover()
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
        manager.stopPeerDiscovery(channel, discoverResultCallback)
    }

    fun startServer() {
        println("start server")
        val sread = Thread(
            Runnable {
                val serverSocket = ServerSocket(8888)
                serverSocket.use {
                    /**

                    Wait for client connections. This call blocks until a
                    connection is accepted from a client.*/
                    val client = serverSocket.accept()
                    println("connection established123")
                    val inputstream = client.getInputStream()
                    val barray = ByteArray(128)
                    val code = inputstream.read(barray)
                    Log.d(TAG, "Read code: $code")
                    Log.d(TAG, "Received: " + barray.toString(Charsets.UTF_8))
                    client.close()
                    serverSocket.close()}}
        )
        sread.start()
    }

    fun connectSocket() {
       println("connect socket")
       val sread = Thread(
           Runnable {
               val socket = Socket()
               socket.bind(null)
               socket.connect((InetSocketAddress(groupOwnerAddress, 8888)), 500)
               val outputstream = socket.getOutputStream()
               outputstream.write("Moin Meister".toByteArray(Charsets.UTF_8).copyOf(128))
               Log.d(TAG, "did the sing")
               socket.close()
           }
       )
        sread.start()
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
        Button(onClick = { activity?.startServer()}) {
            Text("Host")
        }
        Button(onClick = { activity?.connectSocket()}) {
            Text("Connect")
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