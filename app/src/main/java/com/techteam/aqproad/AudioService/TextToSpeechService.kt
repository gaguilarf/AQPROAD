package com.techteam.aqproad.AudioService

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.core.app.NotificationCompat
import com.techteam.aqproad.MainActivity
import com.techteam.aqproad.R
import java.util.Locale

class TextToSpeechService : Service() {
    private lateinit var tts: TextToSpeech
    private var isPlaying = false
    private var isPaused = false

    inner class LocalBinder : Binder() {
        fun getService(): TextToSpeechService = this@TextToSpeechService
    }

    private val binder = LocalBinder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Establecer el idioma a español
                val result = tts.setLanguage(Locale("es", "PE")) // Español de España
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts.language = Locale.US
                    Log.e("TTS", "El idioma español no está soportado.")
                }

                // Configurar el listener para el progreso de la utterance
                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        // Aquí puedes manejar el inicio de la utterance si es necesario
                    }

                    override fun onDone(utteranceId: String?) {
                        isPlaying = false

                        // Enviar un broadcast para notificar que el audio ha terminado
                        val intent = Intent("com.example.tts.ACTION_AUDIO_FINISHED")
                        sendBroadcast(intent)
                        //stopForeground(true)
                    }

                    override fun onError(utteranceId: String?) {
                        // Manejar errores si es necesario
                    }
                })

            } else {
                Log.e("TTS", "Error al inicializar TTS.")
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        when (action) {
            ACTION_START -> {
                val description = intent.getStringExtra(EXTRA_DESCRIPTION) ?: ""
                startForegroundService(description)
            }
            ACTION_PAUSE -> pauseText()
            ACTION_STOP -> stopForegroundService()
            ACTION_CREATE_NOTIFICATION -> {
                val description = intent?.getStringExtra(EXTRA_DESCRIPTION) ?: ""
                startForeground(NOTIFICATION_ID, createNotification(description))
            }
            ACTION_DESTROY_NOTIFICATION -> {
                stopForeground(true) // Eliminar la notificación
            }
        }

        return START_STICKY
    }

    private fun startForegroundService(description: String) {
        tts.speak(description, TextToSpeech.QUEUE_FLUSH, null, "tts_id")
        isPlaying = true
    }

    fun isTtsPlaying(): Boolean {
        return isPlaying
    }

    private fun pauseText() {
        if (isPlaying) {
            tts.stop() // Pausa simulada (Android TTS no tiene pausa real)
            isPlaying = false
        }
    }

    private fun stopForegroundService() {
        tts.stop()
        isPlaying = false
        stopForeground(true)
        stopSelf()
    }

    private fun createNotification(description: String): Notification {
        val stopIntent = Intent(this, TextToSpeechService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        // Crear un Intent para abrir la actividad que contiene el fragmento
        val fragmentIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("EXTRA_OPEN_ITEM_FRAGMENT", "ITEM_FRAGMENT")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val fragmentPendingIntent = PendingIntent.getActivity(this, 1, fragmentIntent, PendingIntent.FLAG_IMMUTABLE)

        val notificationChannelId = createNotificationChannel()

        return NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Text-to-Speech Playing")
            .setContentText("Reproduciendo: $description")
            .setSmallIcon(R.drawable.music_icon)
            .addAction(R.drawable.ic_stop, "Detener", stopPendingIntent)
            .setContentIntent(fragmentPendingIntent) // Establecer el PendingIntent para abrir el fragmento
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel(): String {
        val channelId = "tts_channel"
        val channelName = "Text-to-Speech Notifications"
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        return channelId
    }

    private fun isAppInForeground(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses
        val packageName = packageName
        for (process in appProcesses) {
            if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                process.processName == packageName) {
                return true
            }
        }
        return false
    }

    override fun onDestroy() {
        tts.shutdown()
        super.onDestroy()
        stopForeground(true)
    }

    companion object {
        const val ACTION_START = "com.example.tts.ACTION_START"
        const val ACTION_PAUSE = "com.example.tts.ACTION_PAUSE"
        const val ACTION_STOP = "com.example.tts.ACTION_STOP"
        const val ACTION_CREATE_NOTIFICATION = "com.example.tts.ACTION_CREATE_NOTIFICATION"
        const val ACTION_DESTROY_NOTIFICATION = "com.example.tts.ACTION_DESTROY_NOTIFICATION"
        const val EXTRA_DESCRIPTION = "com.example.tts.EXTRA_DESCRIPTION"
        const val NOTIFICATION_ID = 1
    }
}
