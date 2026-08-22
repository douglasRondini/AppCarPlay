package com.example.appcarplay.util

import java.net.NetworkInterface
import java.util.Collections

/** Retorna o primeiro IPv4 não-loopback da rede local (Wi-Fi), usado para exibir a URL de espelhamento. */
fun getLocalIpAddress(): String? {
    return try {
        val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (intf in interfaces) {
            val addrs = Collections.list(intf.inetAddresses)
            for (addr in addrs) {
                if (!addr.isLoopbackAddress && addr.hostAddress?.contains(':') == false) {
                    return addr.hostAddress
                }
            }
        }
        null
    } catch (e: Exception) {
        null
    }
}
