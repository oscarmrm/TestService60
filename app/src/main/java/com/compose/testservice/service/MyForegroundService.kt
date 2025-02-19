package com.compose.testservice.service

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import com.compose.testservice.R

class MyForegroundService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private var counter = 0
    private lateinit var notificationManager: NotificationManager
    private val notificationId = 1
    private val channelId = "my_channel_id"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        notificationManager = getSystemService(NotificationManager::class.java)
        startForegroundService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        return START_STICKY
    }

    private fun startForegroundService() {
        val notification = createNotification(counter)
        startForeground(notificationId, notification) // Iniciar con la notificación inicial
        handler.post(updateCounterRunnable)
    }

    private val updateCounterRunnable = object : Runnable {
        override fun run() {
            counter++
            Log.d("MyService", "Contador: $counter")
            updateNotification(counter) // Actualizar notificación con el nuevo contador
            handler.postDelayed(this, 1000) // Ejecutar cada segundo
        }
    }

    private fun updateNotification(counter: Int) {
        val notification = createNotification(counter)
        notificationManager.notify(notificationId, notification)
    }

    private fun createNotification(counter: Int): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Servicio en ejecución")
            .setContentText("Contador: $counter segundos")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Servicio en Foreground",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateCounterRunnable)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val restartServiceIntent = Intent(applicationContext, MyForegroundService::class.java).apply {
            setPackage(packageName)
        }
        val restartPendingIntent = PendingIntent.getService(
            this, 1, restartServiceIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 1000, // Reiniciar en 1 segundo
            restartPendingIntent
        )
    }
}