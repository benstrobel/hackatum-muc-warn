package com.example.muc_warn.busineslogic.wifidirect

import java.io.InputStream
import java.io.OutputStream

class NewConnectedPeer(val macAddress: String, val inputStream: InputStream, val outputStream: OutputStream, val closePeer: () -> Unit)