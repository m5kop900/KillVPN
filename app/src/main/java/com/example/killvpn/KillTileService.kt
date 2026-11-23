package com.example.killvpn

import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class KillTileService : TileService() {

    override fun onStartListening() {
        qsTile?.apply {
            state = if (VpnService.prepare(this@KillTileService) == null) Tile.STATE_INACTIVE
                    else Tile.STATE_UNAVAILABLE
            updateTile()
        }
    }

    override fun onClick() {
        super.onClick()

        // هنوز اجازه داده نشده: اپ را باز کن تا کاربر تأیید کند
        if (VpnService.prepare(this) != null) {
            val intent = Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (Build.VERSION.SDK_INT >= 34) {
                val pi = PendingIntent.getActivity(
                    this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                startActivityAndCollapse(pi)
            } else {
                @Suppress("DEPRECATION")
                startActivityAndCollapse(intent)
            }
            return
        }

        startService(Intent(this, KillVpnService::class.java))
    }
}
