package com.example.killvpn

import android.content.Intent
import android.net.VpnService
import android.util.Log

/**
 * Establishing a new VPN interface makes Android drop the currently active VPN.
 * We immediately close our own interface, so no VPN remains active afterwards.
 */
class KillVpnService : VpnService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            Builder()
                .setSession("kill")
                .addAddress("10.255.255.1", 32)
                .establish()
                ?.close()
        } catch (e: Exception) {
            Log.w(TAG, "establish failed", e)
        } finally {
            stopSelf(startId)
        }
        return START_NOT_STICKY
    }

    companion object {
        private const val TAG = "KillVpnService"
    }
}
