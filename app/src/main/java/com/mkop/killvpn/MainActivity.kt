package com.mkop.killvpn

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        status = TextView(this).apply {
            gravity = Gravity.CENTER
            textSize = 16f
            setPadding(48, 48, 48, 48)
        }
        val button = Button(this).apply {
            text = getString(R.string.btn_grant)
            setOnClickListener { requestPermission() }
        }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            addView(status)
            addView(button)
        }
        setContentView(root)
        refreshStatus()
    }

    override fun onResume() {
        super.onResume()
        refreshStatus()
    }

    private fun requestPermission() {
        val intent = VpnService.prepare(this)
        if (intent == null) refreshStatus() else startActivityForResult(intent, REQ_VPN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_VPN) refreshStatus()
    }

    private fun refreshStatus() {
        status.text = getString(
            if (VpnService.prepare(this) == null) R.string.status_ready
            else R.string.status_need_permission
        )
    }

    companion object {
        private const val REQ_VPN = 1
    }
}
