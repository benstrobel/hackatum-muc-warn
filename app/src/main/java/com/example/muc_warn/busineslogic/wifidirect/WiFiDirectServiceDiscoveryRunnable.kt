package com.example.muc_warn.busineslogic.wifidirect

import android.os.Handler

class WiFiDirectServiceDiscoveryRunnable(val handler: Handler, val callback: () -> Unit, val timeout: Long): Runnable {
    override fun run() {
        callback()
        handler.postDelayed(this, timeout)
    }
}