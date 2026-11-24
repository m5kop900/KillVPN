package com.mkop.killvpn

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.VpnService
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast

/**
 * Establishing a new VPN interface makes Android drop the currently active VPN.
 * We close our own interface right away, so no VPN remains active afterwards.
 */
class KillVpnService : VpnService() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val before = isVpnActive()

        val established = try {
            val pfd = Builder()
                .setSession("kill")
                .addAddress("10.255.255.1", 32)
                .establish()
            pfd?.close()
            pfd != null
        } catch (e: Exception) {
            Log.w(TAG, "Could not establish the temporary VPN", e)
            false
        }

        if (!established) {
            toast(R.string.toast_no_permission)
            stopSelf(startId)
            return START_NOT_STICKY
        }

        // Give Android a moment to tear the old VPN down, then report what happened.
        handler.postDelayed({
            val after = isVpnActive()
            toast(
                when {
                    after == true -> R.string.toast_still_active
                    before == false -> R.string.toast_no_vpn
                    else -> R.string.toast_disconnected
                }
            )
            stopSelf(startId)
        }, CHECK_DELAY_MS)

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    /** true / false if a VPN network is up, null if it can't be determined. */
    @Suppress("DEPRECATION")
    private fun isVpnActive(): Boolean? = try {
        val cm = getSystemService(ConnectivityManager::class.java)
        cm.allNetworks.any { network ->
            cm.getNetworkCapabilities(network)
                ?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true
        }
    } catch (e: Exception) {
        null
    }

    private fun toast(resId: Int) {
        Toast.makeText(LocaleHelper.wrap(this), resId, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val TAG = "KillVpnService"
        private const val CHECK_DELAY_MS = 700L
    }
}
