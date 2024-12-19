package com.techteam.aqproad.AudioService

import TextToSpeechManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.techteam.aqproad.MainActivity
import com.techteam.aqproad.R

class TextToSpeechService : Service() {

    companion object {
        const val CHANNEL_ID = "TextToSpeechChannel"
        const val ACTION_PLAY_PAUSE = "com.techteam.aqproad.ACTION_PLAY_PAUSE"
        const val ACTION_STOP = "com.techteam.aqproad.ACTION_STOP"
        const val EXTRA_TEXT = "com.techteam.aqproad.EXTRA_TEXT"
        var isServiceRunning = false
    }

    private lateinit var textToSpeechManager: TextToSpeechManager
    private var isPlaying = false
    private var currentText = ""
    private var isAppInForeground = true


    override fun onCreate() {
        super.onCreate()
        textToSpeechManager = TextToSpeechManager(this)
        createNotificationChannel()
        isServiceRunning = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_PLAY_PAUSE -> handlePlayPause()
                ACTION_STOP -> handleStop()
                "foreground_status"-> setAppInForeground(true)
                "background_status" -> setAppInForeground(false)
                "update_status" ->  updateNotificationVisibility()
                else -> {
                    currentText = it.getStringExtra(EXTRA_TEXT) ?: ""
                    startTextToSpeech()
                }
            }
        }
        return START_STICKY  // Asegura que el servicio se reinicie si se detiene por el sistema
    }

    private fun startTextToSpeech() {
        if (currentText.isNotEmpty()) {
            textToSpeechManager.initialize { isInitialized ->
                if (isInitialized) {
                    textToSpeechManager.speak(currentText)
                    isPlaying = true
                    updateNotificationVisibility()
                }
            }
        }
    }

    private fun handlePlayPause() {
        if (textToSpeechManager.isSpeaking()) {
            textToSpeechManager.stop()
            isPlaying = false
        } else {
            if (currentText.isNotEmpty()) {
                textToSpeechManager.speak(currentText)
                isPlaying = true
            }
        }
        updateNotificationVisibility()
    }

    private fun handleStop() {
        textToSpeechManager.stop()
        stopForeground(true)
        stopSelf()
        isServiceRunning = false
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Text-to-Speech Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }
    private fun updateNotificationVisibility() {
        if (isAppInForeground || !isPlaying) {
            stopForeground(true) // Ocultar la notificación si la app está en primer plano o no esta reproduciendo
        } else {
            startForeground(1, buildNotification()) // Mostrar la notificación si la app está en segundo plano y esta reproduciendo
        }
    }


    private fun buildNotification(): Notification {
        val playPauseIntent = Intent(this, TextToSpeechService::class.java).apply {
            action = ACTION_PLAY_PAUSE
        }
        val playPausePendingIntent = PendingIntent.getService(
            this, 0, playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val stopIntent = Intent(this, TextToSpeechService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val activityIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val activityPendingIntent = PendingIntent.getActivity(
            this, 0, activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseIcon =
            if (textToSpeechManager.isSpeaking()) R.drawable.ic_pause else R.drawable.ic_play


        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Text-to-Speech")
            .setContentText(currentText.take(50))
            .setSmallIcon(R.drawable.ic_play)
            .setContentIntent(activityPendingIntent)
            .addAction(playPauseIcon, if (isPlaying) "Pause" else "Play", playPausePendingIntent)
            .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
            .setOngoing(true)


        return builder.build()
    }
    fun setAppInForeground(inForeground: Boolean){
        isAppInForeground = inForeground
        updateNotificationVisibility()
    }


    override fun onDestroy() {
        super.onDestroy()
        textToSpeechManager.shutdown()
        isServiceRunning = false
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null //No necesitamos vinculación
    }
}