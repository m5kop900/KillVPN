package com.mkop.killvpn

import android.annotation.TargetApi
import android.app.Activity
import android.app.StatusBarManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var statusDot: View
    private lateinit var statusTitle: TextView
    private lateinit var statusDesc: TextView
    private lateinit var grantButton: View
    private lateinit var disconnectButton: View
    private lateinit var langEn: TextView
    private lateinit var langFa: TextView

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusDot = findViewById(R.id.status_dot)
        statusTitle = findViewById(R.id.status_title)
        statusDesc = findViewById(R.id.status_desc)
        grantButton = findViewById(R.id.btn_grant)
        disconnectButton = findViewById(R.id.btn_disconnect)
        langEn = findViewById(R.id.lang_en)
        langFa = findViewById(R.id.lang_fa)

        grantButton.setOnClickListener { requestVpnPermission() }
        disconnectButton.setOnClickListener {
            startService(Intent(this, KillVpnService::class.java))
        }
        findViewById<View>(R.id.btn_vpn_settings).setOnClickListener { openVpnSettings() }

        val addTileButton = findViewById<View>(R.id.btn_add_tile)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            addTileButton.visibility = View.VISIBLE
            addTileButton.setOnClickListener { requestAddTile() }
        }

        val current = LocaleHelper.current(this)
        langEn.isSelected = current == LocaleHelper.EN
        langFa.isSelected = current == LocaleHelper.FA
        langEn.setOnClickListener { setLanguage(LocaleHelper.EN) }
        langFa.setOnClickListener { setLanguage(LocaleHelper.FA) }

        findViewById<TextView>(R.id.version).text =
            getString(R.string.version_format, BuildConfig.VERSION_NAME)
    }

    override fun onResume() {
        super.onResume()
        refreshState()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_VPN) refreshState()
    }

    private fun refreshState() {
        val granted = VpnService.prepare(this) == null
        statusDot.setBackgroundResource(if (granted) R.drawable.bg_dot_ok else R.drawable.bg_dot_warn)
        statusTitle.setText(if (granted) R.string.status_title_ready else R.string.status_title_need)
        statusDesc.setText(if (granted) R.string.status_desc_ready else R.string.status_desc_need)
        grantButton.visibility = if (granted) View.GONE else View.VISIBLE
        disconnectButton.visibility = if (granted) View.VISIBLE else View.GONE
    }

    private fun requestVpnPermission() {
        val intent = VpnService.prepare(this)
        if (intent == null) refreshState() else startActivityForResult(intent, REQ_VPN)
    }

    private fun setLanguage(lang: String) {
        if (lang == LocaleHelper.current(this)) return
        LocaleHelper.save(this, lang)
        recreate()
    }

    private fun openVpnSettings() {
        try {
            startActivity(Intent(Settings.ACTION_VPN_SETTINGS))
        } catch (e: ActivityNotFoundException) {
            // No VPN settings screen on this device; nothing to do.
        }
    }

    /** Android 13+: ask the system to add the Quick Settings tile with one tap. */
    @TargetApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestAddTile() {
        val manager = getSystemService(StatusBarManager::class.java) ?: return
        manager.requestAddTileService(
            ComponentName(this, KillTileService::class.java),
            getString(R.string.tile_label),
            Icon.createWithResource(this, R.drawable.ic_tile),
            mainExecutor
        ) { result ->
            when (result) {
                StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED -> toast(R.string.tile_added)
                StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED -> toast(R.string.tile_already)
                else -> Unit
            }
        }
    }

    private fun toast(resId: Int) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQ_VPN = 1
    }
}
