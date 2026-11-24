package com.mkop.killvpn

import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.SystemClock
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class KillTileService : TileService() {

    private var armedUntil = 0L

    override fun onStartListening() {
        armedUntil = 0L
        refreshTile(armed = false)
    }

    override fun onStopListening() {
        armedUntil = 0L
    }

    override fun onClick() {
        super.onClick()

        // VPN permission not granted yet: open the app so the user can approve it.
        if (VpnService.prepare(this) != null) {
            val intent = Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (Build.VERSION.SDK_INT >= 34) {
                val pi = PendingIntent.getActivity(
                    this, 0, intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                startActivityAndCollapse(pi)
            } else {
                @Suppress("DEPRECATION")
                startActivityAndCollapse(intent)
            }
            return
        }

        val now = SystemClock.elapsedRealtime()
        if (now > armedUntil) {
            // First tap: only arm the tile, nothing is disconnected yet.
            armedUntil = now + CONFIRM_WINDOW_MS
            refreshTile(armed = true)
        } else {
            // Second tap inside the window: disconnect.
            armedUntil = 0L
            startService(Intent(this, KillVpnService::class.java))
            refreshTile(armed = false)
        }
    }

    private fun refreshTile(armed: Boolean) {
        val localized = LocaleHelper.wrap(this)
        qsTile?.apply {
            state = if (armed) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = localized.getString(if (armed) R.string.tile_confirm else R.string.tile_label)
            updateTile()
        }
    }

    companion object {
        private const val CONFIRM_WINDOW_MS = 3000L
    }
}
