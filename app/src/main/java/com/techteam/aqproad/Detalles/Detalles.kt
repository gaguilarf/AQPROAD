package com.techteam.aqproad.Detalles

import android.content.Context
import android.content.pm.ActivityInfo
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.SeekBar
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.techteam.aqproad.R

class DetallesActivity : AppCompatActivity()  {

    private lateinit var videoView: VideoView
    private lateinit var playPauseButton: ImageButton
    private lateinit var ratingBar: RatingBar
    private lateinit var fullScreenBtn: ImageButton
    private lateinit var volumeSeekBar: SeekBar
    private lateinit var audioManager: AudioManager
    private lateinit var volumeObserver: ContentObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles)

        // Inicializar los elementos del layout
        videoView = findViewById(R.id.videoView)
        playPauseButton = findViewById(R.id.buttonPlayPause)
        ratingBar = findViewById(R.id.ratingBar)
        fullScreenBtn = findViewById(R.id.buttonFullScreen)
        volumeSeekBar = findViewById(R.id.seekBarVolume)

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Configurar el rango del SeekBar basado en el volumen máximo del sistema
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        volumeSeekBar.max = maxVolume
        volumeSeekBar.progress = currentVolume

        // Escuchar cambios en el SeekBar de volumen
        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Ajustar el volumen del sistema
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Configurar el video (ejemplo: cargar desde una URL o recurso local)
        videoView.setVideoPath("android.resource://" + packageName + "/" + R.raw.v1)

        // Control de reproducción/pausa
        playPauseButton.setOnClickListener {
            if (videoView.isPlaying) {
                videoView.pause()
                playPauseButton.setImageResource(android.R.drawable.ic_media_play)
            } else {
                videoView.start()
                playPauseButton.setImageResource(android.R.drawable.ic_media_pause)
            }
        }


        fullScreenBtn.setOnClickListener {
            //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            toggleFullScreen()
            //isFullScreen = !isFullScreen
        }

        // Calificación
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            // Manejar el valor de la calificación
        }

        // Registrar el ContentObserver para observar los cambios de volumen
        volumeObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                // Actualizar el SeekBar cuando cambie el volumen del sistema
                val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                volumeSeekBar.progress = currentVolume
            }
        }
        contentResolver.registerContentObserver(
            Settings.System.CONTENT_URI,
            true,
            volumeObserver
        )
    }

    private fun toggleFullScreen() {
        val orientacion = resources.configuration.orientation
        requestedOrientation = if (orientacion == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            //videoControls.visibility = View.GONE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        }
    }

    override fun onResume() {
        super.onResume()
        // Actualiza la posición del SeekBar cuando se reanuda la actividad
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        volumeSeekBar.progress = currentVolume
    }
}