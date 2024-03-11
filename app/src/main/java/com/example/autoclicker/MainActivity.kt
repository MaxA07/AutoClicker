package com.example.autoclicker

import android.R
import android.bluetooth.BluetoothClass.Device
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_SYSTEM_ALERT_WINDOW = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (checkSystemAlertWindowPermission()) {
            startFloatingViewService()
        } else {
            requestSystemAlertWindowPermission()
        }

        // Финиш активити, чтобы ее не отображать
        finish()
    }

    private fun checkSystemAlertWindowPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }

    private fun requestSystemAlertWindowPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:$packageName")
        startActivityForResult(intent, REQUEST_CODE_SYSTEM_ALERT_WINDOW)
    }

    private fun startFloatingViewService() {
        val serviceIntent = Intent(this, FloatingViewService::class.java)
        startService(serviceIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SYSTEM_ALERT_WINDOW) {
            if (checkSystemAlertWindowPermission()) {
                startFloatingViewService()
            } else {
                Toast.makeText(this, "Не предоставлено разрешение", Toast.LENGTH_SHORT).show()
            }
        }
    }
}