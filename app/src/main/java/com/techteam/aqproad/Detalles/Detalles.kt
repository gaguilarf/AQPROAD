package com.techteam.aqproad.Detalles

import android.os.Bundle
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.SeekBar
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.techteam.aqproad.R

class DetallesActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var playPauseButton: ImageButton
    private lateinit var volumeSeekBar: SeekBar
    private lateinit var ratingBar: RatingBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles)

        // Inicializar los elementos del layout
        videoView = findViewById(R.id.videoView)
        playPauseButton = findViewById(R.id.buttonPlayPause)
        volumeSeekBar = findViewById(R.id.seekBarVolume)
        ratingBar = findViewById(R.id.ratingBar)

        // Configurar el video (ejemplo: cargar desde una URL o recurso local)
        videoView.setVideoPath("URL_DEL_VIDEO_O_RUTA_LOCAL")

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

        // Control de volumen
        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val volume = progress / 100.0f
                videoView.setVolume(volume, volume)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Calificación
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            // Aquí puedes manejar el valor de la calificación
            // Solo permitir calificación si está logeado y en el lugar correcto
        }
    }
}