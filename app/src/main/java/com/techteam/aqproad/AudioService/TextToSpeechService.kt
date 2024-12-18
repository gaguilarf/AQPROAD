package com.techteam.aqproad.AudioService

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.techteam.aqproad.MainActivity
import com.techteam.aqproad.R

class TextToSpeechService : Service() {

    private lateinit var textToSpeechManager: TextToSpeechManager
    private val CHANNEL_ID = "TTS_SERVICE_CHANNEL"
    private val NOTIFICATION_ID = 1
    private var currentText = ""

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_SPEAK = "ACTION_SPEAK"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_TEXT = "EXTRA_TEXT"
    }

    override fun onCreate() {
        super.onCreate()
        textToSpeechManager = TextToSpeechManager(applicationContext)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START ->{
                startTtsService()
                Log.d("TTS", "Servicio TTS iniciado")
            }
            ACTION_SPEAK -> {
                val text = intent.getStringExtra(EXTRA_TEXT) ?: ""
                if(text.isNotEmpty()){
                    currentText = text
                    speak(text)
                    Log.d("TTS", "TTS hablando: $text")
                } else{
                    Log.e("TTS","Texto vacio")
                }

            }
            ACTION_STOP -> {
                stopTtsService()
                Log.d("TTS", "Servicio TTS detenido")
            }
        }
        return START_STICKY
    }

    private fun startTtsService() {
        textToSpeechManager.initialize { isInitialized ->
            if (isInitialized) {
                startForegroundService()
                Log.d("TTS","Servicio TTS inicializado correctamente")
            } else {
                Log.e("TTS","Error al inicializar el TTS")
                stopSelf()
            }
        }
    }

    private fun speak(text: String) {
        textToSpeechManager.speak(text)
        startForegroundService()
    }

    private fun stopTtsService() {
        textToSpeechManager.shutdown()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "TTS Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun startForegroundService() {
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("TTS Service")
            .setContentText("Reproduciendo: $currentText")
            .setSmallIcon(R.drawable.ic_play)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
            .addAction(R.drawable.ic_stop, "Detener", createStopPendingIntent())
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createStopPendingIntent(): PendingIntent {
        val stopIntent = Intent(this, TextToSpeechService::class.java).apply {
            action = ACTION_STOP
        }
        return PendingIntent.getService(
            this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TTS", "Servicio TTS destruido")
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}