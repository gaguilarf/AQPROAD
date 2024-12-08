package com.techteam.aqproad.AudioService

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.techteam.aqproad.R

class AudioService : Service() {

    // Constantes y variables globales
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "AudioServiceChannel"
    private val binder = AudioBinder()
    lateinit var mediaPlayer: MediaPlayer
    var isPlaying = false
    private val handler = Handler(Looper.getMainLooper())

    // Clase interna para vincular el servicio
    inner class AudioBinder : Binder() {
        fun getService(): AudioService = this@AudioService
    }

    // Métodos del ciclo de vida del servicio
    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        initializeMediaPlayer()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleIntentAction(intent?.action)
        if (intent?.action != "STOP") {
            updateNotification() // Actualiza la notificación si no es STOP
        }
        return START_STICKY
    }

    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }

    // Métodos principales del MediaPlayer
    private fun initializeMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.self_care).apply {
            isLooping = true
        }
    }

    private fun playAudio() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            isPlaying = true
        }
    }

    private fun pauseAudio() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            isPlaying = false
        }
    }

    private fun stopAudio() {
        if (isPlaying || mediaPlayer.currentPosition > 0) {
            mediaPlayer.stop()
            mediaPlayer.prepare() // Prepara el MediaPlayer para un nuevo inicio
            mediaPlayer.seekTo(0) // Mueve la posición al inicio
            isPlaying = false
        }
        stopForeground(true) // Detiene la notificación
        stopSelf() // Detiene el servicio
    }

    // Enviar un broadcast con el estado actual
    private fun sendStateUpdateBroadcast() {
        val intent = Intent("com.mv.audioprueba.STATE_UPDATE")
        intent.putExtra("isPlaying", isPlaying)
        sendBroadcast(intent)
    }

    // Métodos relacionados con la notificación
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Audio Service Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Canal para el reproductor de audio"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun updateNotification() {
        // Acción Play/Pause con texto
        val playPauseAction = NotificationCompat.Action.Builder(
            0,  // No usamos icono
            if (isPlaying) "Pausar" else "Reproducir",  // Texto que cambiará dependiendo si está reproduciendo o no
            createPendingIntent(if (isPlaying) "PAUSE" else "PLAY", 1)
        ).build()

        // Acción Stop con texto
        val stopAction = NotificationCompat.Action.Builder(
            0,  // No usamos icono
            "Detenerse",  // Texto "Detenerse"
            createPendingIntent("STOP", 2)
        ).build()

        // Construcción de la notificación
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Reproductor de Audio")
            .setContentText(if (isPlaying) "Reproduciendo" else "La reproducción se detuvo")
            .setSmallIcon(R.drawable.music_icon)  // Icono pequeño
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)  // Mantener notificación en pantalla
            .addAction(playPauseAction)  // Añadir acción de Play/Pause
            .addAction(stopAction)  // Añadir acción de Stop
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createPendingIntent(action: String, requestCode: Int): PendingIntent {
        val intent = Intent(this, AudioService::class.java).apply { this.action = action }
        return PendingIntent.getService(this, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun handleIntentAction(action: String?) {
        when (action) {
            "PLAY" -> playAudio()
            "PAUSE" -> pauseAudio()
            "STOP" -> stopAudio()
        }
    }
}