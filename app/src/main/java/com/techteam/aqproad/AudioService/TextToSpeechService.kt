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
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.core.app.NotificationCompat
import com.techteam.aqproad.MainActivity
import com.techteam.aqproad.R
import java.util.Locale

class TextToSpeechService : Service(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech
    private val CHANNEL_ID = "TTS_SERVICE_CHANNEL"
    private val NOTIFICATION_ID = 1
    private var currentText = ""
    private var isInitialized = false
    var isSpeaking = false
    private var isPlaying = false

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_SPEAK = "ACTION_SPEAK"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_STOP_NOTIFICATION = "ACTION_STOP_NOTIFICATION"
        const val EXTRA_TEXT = "EXTRA_TEXT"
    }

    override fun onCreate() {
        super.onCreate()
        textToSpeech = TextToSpeech(this, this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                startTtsService()
                Log.d("TTSX", "Servicio TTS iniciado")
            }
            ACTION_SPEAK -> {
                val text = intent.getStringExtra(EXTRA_TEXT) ?: ""
                if(text.isNotEmpty()){
                    currentText = text
                    if(isInitialized){
                        speak(text)
                        isSpeaking = true
                        Log.d("TTS", "TTS hablando: $text")
                    }
                    else{
                        Log.e("TTS","TTS no inicializado")
                    }
                } else{
                    Log.e("TTS","Texto vacio")
                }

            }
            ACTION_STOP -> {
                stopTtsService()
                isSpeaking = false
                Log.d("TTS", "Servicio TTS detenido")
            }
            ACTION_STOP_NOTIFICATION -> {
                if (!isSpeaking) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                }
            }
        }
        return START_STICKY
    }

    private fun startTtsService() {
        //No initialization logic is needed here, it is handled by onInit
        //We can log that this service started
        Log.d("TTS", "Servicio TTS inicializado correctamente")
    }

    private fun speak(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
        startForegroundService()
        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                isSpeaking = true
            }

            override fun onDone(utteranceId: String?) {
                isSpeaking = false
                if (!isAppInForeground()) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                }
            }

            override fun onError(utteranceId: String?) {
                isSpeaking = false
                Log.e("TTS", "Error speaking text: $utteranceId")
            }
        })
    }

    fun startText(description: String) {
        if (!isPlaying) {
            textToSpeech.speak(description, TextToSpeech.QUEUE_FLUSH, null, "tts_id")
        }
    }

    fun pauseText() {
        if (isPlaying) {
            // Nota: Android TTS no soporta pausa nativa. Podrías detener el TTS aquí.
            textToSpeech.stop()
            isPlaying = false
        }
    }

    fun stopText() {
        textToSpeech.stop()
        isPlaying = false
    }

    // Inicia o reanuda el servicio
    fun startOrResumeTts(description: String) {
        val intent = Intent(this, TextToSpeechService::class.java)
        bindService(intent, connection, BIND_AUTO_CREATE)
        startService(intent)
        textToSpeech?.startText(description)
    }

    // Pausa la reproducción
    fun pauseTtsPlayback() {
        textToSpeechService?.pauseText()
    }

    // Detiene la reproducción
    fun stopTtsPlayback() {
        textToSpeechService?.stopText()
        unbindService(connection)
        stopService(Intent(this, TextToSpeechService::class.java))
    }

    private var textToSpeechService: TextToSpeechService? = null
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            textToSpeechService = (service as TextToSpeechService.LocalBinder).getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            textToSpeechService = null
        }
    }

    private fun stopTtsService() {
        textToSpeech.stop()
        if (::textToSpeech.isInitialized) {
            textToSpeech.shutdown()
        }
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
        // No mostrar notificación si la app está en primer plano
        if (isAppInForeground()) {
            return
        }

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
        if (::textToSpeech.isInitialized) {
            textToSpeech.shutdown()
        }
    }

    fun isTtsPlaying(): Boolean {
        return isSpeaking
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // set language
            val result = textToSpeech.setLanguage(Locale("es", "ES"))

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Lenguaje no soportado")
            } else {
                isInitialized = true
                Log.d("TTS", "TTS inicializado en Oninit")
            }
        } else {
            Log.e("TTS", "TTS Initialization failed")
        }
    }

    private fun isAppInForeground(): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = applicationContext.packageName

        for (process in appProcesses) {
            if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                && process.processName == packageName) {
                return true
            }
        }
        return false
    }
}