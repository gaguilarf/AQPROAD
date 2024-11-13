package com.techteam.aqproad.Detalles

import android.Manifest
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.SeekBar
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.techteam.aqproad.R
import android.location.Location

class DetallesActivity : AppCompatActivity()  {

    private lateinit var videoView: VideoView
    private lateinit var playPauseButton: ImageButton
    private lateinit var ratingBar: RatingBar
    private lateinit var fullScreenBtn: ImageButton
    private lateinit var volumeSeekBar: SeekBar
    private lateinit var audioManager: AudioManager
    private lateinit var volumeObserver: ContentObserver
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var isInFullScreen = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
            togglePlayPause()
        }

        // Control de pantalla completa
        fullScreenBtn.setOnClickListener {
            toggleFullScreen()
        }

        // Calificación
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            // Manejar el valor de la calificación
            if (!isUserLoggedIn()) {
                showToast("Debe iniciar sesión para calificar.")
                ratingBar.rating = 0f
                return@setOnRatingBarChangeListener
            }

            if (!isUserAtLocation()) {
                showToast("Debe estar en la ubicación de la edificación para calificar.")
                ratingBar.rating = 0f
                return@setOnRatingBarChangeListener
            }

            saveUserRating(rating)
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

    private fun togglePlayPause() {
        if (videoView.isPlaying) {
            videoView.pause()
            playPauseButton.setImageResource(android.R.drawable.ic_media_play)
        } else {
            videoView.start()
            playPauseButton.setImageResource(android.R.drawable.ic_media_pause)
        }
    }

    private fun toggleFullScreen() {
        if (isInFullScreen) {
            exitFullScreen()
        } else {
            enterFullScreen()
        }
        isInFullScreen = !isInFullScreen
    }

    private fun enterFullScreen() {
        setContentView(R.layout.fullscreen_layout)  // Cambia al layout de pantalla completa

        val fullscreenVideoView = findViewById<VideoView>(R.id.fullscreenVideoView)
        val buttonPlayPauseFullscreen = findViewById<ImageButton>(R.id.buttonPlayPauseFullscreen)
        val buttonExitFullscreen = findViewById<ImageButton>(R.id.buttonExitFullscreen)

        // Configura el video en pantalla completa
        fullscreenVideoView.setVideoPath("android.resource://" + packageName + "/" + R.raw.v1)
        fullscreenVideoView.seekTo(videoView.currentPosition)  // Mantiene la posición actual del video
        fullscreenVideoView.start()

        // Configura los controles
        buttonPlayPauseFullscreen.setOnClickListener {
            if (fullscreenVideoView.isPlaying) {
                fullscreenVideoView.pause()
                buttonPlayPauseFullscreen.setImageResource(android.R.drawable.ic_media_play)
            } else {
                fullscreenVideoView.start()
                buttonPlayPauseFullscreen.setImageResource(android.R.drawable.ic_media_pause)
            }
        }

        buttonExitFullscreen.setOnClickListener {
            exitFullScreen()
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun exitFullScreen() {
        setContentView(R.layout.activity_detalles)  // Vuelve al diseño principal

        // Configura el video y los controles principales en el layout de detalles
        setupMainLayoutControls()
        videoView.seekTo(videoView.currentPosition) // Mantiene la posición actual del video
        videoView.start()  // Reanuda el video en el layout principal

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        isInFullScreen = false
    }

    private fun setupMainLayoutControls() {
        videoView = findViewById(R.id.videoView)
        playPauseButton = findViewById(R.id.buttonPlayPause)
        fullScreenBtn = findViewById(R.id.buttonFullScreen)

        playPauseButton.setOnClickListener {
            togglePlayPause()
        }

        fullScreenBtn.setOnClickListener {
            toggleFullScreen()
        }
    }

    override fun onResume() {
        super.onResume()
        // Actualiza la posición del SeekBar cuando se reanuda la actividad
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        volumeSeekBar.progress = currentVolume
    }

    private fun isUserLoggedIn(): Boolean {
        // Verificar si hay un usuario logeado, por ejemplo, usando SharedPreferences o tu sistema de autenticación.
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    private fun isUserAtLocation(): Boolean {
        // Coordenadas del lugar de la edificación (ejemplo)
        val targetLatitude = -16.39889
        val targetLongitude = -71.535
        var userAtLocation = false

        // Verificar si los permisos de ubicación están otorgados
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val distance = FloatArray(1)
                    Location.distanceBetween(targetLatitude, targetLongitude, it.latitude, it.longitude, distance)
                    userAtLocation = distance[0] < 100 // Distancia máxima en metros
                }
            }
        } else {
            // Solicitar permisos de ubicación
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
        return userAtLocation
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, puedes continuar con la lógica de ubicación
                isUserAtLocation()
            } else {
                showToast("Permiso de ubicación denegado. No se puede verificar la ubicación.")
            }
        }
    }

    private fun saveUserRating(rating: Float) {
        showToast("Gracias por calificar con $rating estrellas.")
        // Guardar la calificación en la base de datos o realizar otra acción
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        contentResolver.unregisterContentObserver(volumeObserver)
    }
}