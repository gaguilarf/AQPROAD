package com.techteam.aqproad.Detalles

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.SeekBar
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.techteam.aqproad.R
import android.util.AttributeSet

class DetallesActivity : AppCompatActivity()  {

    private lateinit var videoView: VideoView
    private lateinit var playPauseButton: ImageButton
    private lateinit var volumeSeekBar: SeekBar
    private lateinit var ratingBar: RatingBar
    private lateinit var fullScreenBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles)

        // Inicializar los elementos del layout
        videoView = findViewById(R.id.videoView)
        playPauseButton = findViewById(R.id.buttonPlayPause)
        volumeSeekBar = findViewById(R.id.seekBarVolume)
        ratingBar = findViewById(R.id.ratingBar)
        fullScreenBtn = findViewById(R.id.buttonFullScreen)

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
            val orientation = resources.configuration.orientation
            
        }

        // Control de volumen
        videoView.setOnPreparedListener { mediaPlayer ->
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

            // Configura la SeekBar para controlar el volumen
            volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                    val volume = (progress / 100.0f) * maxVolume

                    // Ajusta el volumen del MediaPlayer
                    mediaPlayer.setVolume(volume, volume)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        // Calificación
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            // Manejar el valor de la calificación
            // Permitir calificación si está logeado y en el lugar correcto
        }
    }
}