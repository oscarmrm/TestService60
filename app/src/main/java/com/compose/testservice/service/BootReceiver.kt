package com.compose.testservice.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Comprobar si el servicio está corriendo para evitar que se lance cuando ya está activo
            if (!isServiceRunning(context, MyForegroundService::class.java)) {
                val serviceIntent = Intent(context, MyForegroundService::class.java)
                ContextCompat.startForegroundService(context, serviceIntent)
            }
        }
    }
}