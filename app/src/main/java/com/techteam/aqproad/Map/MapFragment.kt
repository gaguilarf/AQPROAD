package com.techteam.aqproad.Map

import android.Manifest
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.techteam.aqproad.R
import com.techteam.aqproad.databinding.FragmentMapBinding
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import android.location.LocationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.techteam.aqproad.MainActivity
import kotlin.math.*

class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mapView: MapView
    private lateinit var db: FirebaseFirestore
    private var sitiosGeo: MutableList<Pair<GeoPoint, String>> = mutableListOf()
    private lateinit var userMarker: Marker
    private var userMarker2: Marker? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private var hasRedirectedToSettings = false
    private var isFirstLocationUpdate = true
    private val notifiedSites: MutableSet<String> = mutableSetOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        val view = binding.root

        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        mapView = binding.mapFragmentView
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(17.0)
        mapView.controller.setCenter(GeoPoint(-16.39983758815227, -71.53672281466419))

        db = FirebaseFirestore.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Verificar permisos de ubicación
        context?.let {
            if (ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                checkGPSAndLoadLocation()
            }
        }

        // Cargar sitios turísticos desde Firestore
        loadTouristSites()

        return view
    }

    private fun simulateUserMovement() {
        // Punto inicial ficticio
        var userLocation = GeoPoint(-16.39983758815227, -71.53572281466418) // Coordenadas de inicio
        val step = 0.0001 // Movimiento en longitud
        val nearbyDistance = 35 // Distancia cercana para disparar la notificación

        // Crea el marcador solo una vez y lo agrega al mapa
        if (!::userMarker.isInitialized) {
            userMarker = Marker(mapView)
            userMarker.position = userLocation
            userMarker.title = "Posición actual"
            userMarker.icon = ContextCompat.getDrawable(requireContext(), R.drawable.icon_user_marker) // Asegúrate de tener un drawable para el marcador
            userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView.overlays.add(userMarker)
            mapView.invalidate()
        }

        // Simula el movimiento indefinidamente con un intervalo de 2 segundos
        val handler = android.os.Handler(requireContext().mainLooper)
        val runnable = object : Runnable {
            override fun run() {
                // Actualiza la ubicación del usuario
                userLocation = GeoPoint(userLocation.latitude, userLocation.longitude - step)
                userMarker.position = userLocation
                mapView.invalidate()

                // Verifica si el usuario está cerca de algún sitio turístico
                sitiosGeo.forEach {
                    val distance = calcularDistancia(userLocation.latitude,userLocation.longitude, it.first.latitude, it.first.longitude)
                    Log.d("disxy", "Distancia: $distance")
                    if (distance <= nearbyDistance) {
                        // Llama a la función para mostrar la notificación si está cerca de un sitio turístico
                        sendNotification("¡Estás cerca de un sitio turístico!", it.second)
                        return@forEach // Sale de la lambda, pero no del bucle for
                    }
                }
                handler.postDelayed(this, 1000)
            }
        }

        handler.post(runnable)
    }

    private fun sendNotification(title: String, content: String) {
        if (isAdded) {
            val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            val channelId = "tourist_site_channel"

            if (notificationManager != null) {
                // Crear el canal de notificación si no existe
                val channel = NotificationChannel(
                    channelId,
                    "Sitios Turísticos Cercanos",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)

                // Crear un PendingIntent para navegar al fragmento de mapa
                val intent = Intent(requireContext(), MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("navigate_to", "MapFragment")
                }
                val pendingIntent = PendingIntent.getActivity(
                    requireContext(),
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // Crear la notificación
                val notification = Notification.Builder(requireContext(), channelId)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.icon_marker)
                    .setContentIntent(pendingIntent) // Asocia el PendingIntent
                    .setAutoCancel(true)
                    .build()

                // Mostrar la notificación
                notificationManager.notify(1, notification)
            }
        }
    }

    private fun loadCurrentLocation() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000 // Cada 5 segundos
            fastestInterval = 2000 // Intervalo más rápido
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    val userLocation = GeoPoint(location.latitude, location.longitude)

                    // Si el marcador ya existe, actualiza su posición
                    if (userMarker2 != null) {
                        userMarker2?.position = userLocation
                        if (isFirstLocationUpdate) {
                            mapView.controller.setCenter(userLocation)
                            isFirstLocationUpdate = false
                        }
                    } else {
                        // Si no existe, crea un nuevo marcador
                        userMarker2 = Marker(mapView).apply {
                            position = userLocation
                            icon = ContextCompat.getDrawable(requireContext(), R.drawable.icon_user_marker) // Asegúrate de tener un icono adecuado
                            title = "Estás aquí"
                            mapView.overlays.add(this) // Agrega el marcador al mapa
                        }
                    }

                    mapView.invalidate() // Refresca el mapa para mostrar los cambios
                }
            }
        }

        // Verificar permisos antes de solicitar actualizaciones de ubicación
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback!!, null)
        }
    }

    private fun loadTouristSites() {
        db.collection("Sitios_turisticos")
            .orderBy("sitID") // Ordenar por sitID
            .get()
            .addOnSuccessListener { documents ->
                sitiosGeo = mutableListOf()

                // Iteramos sobre los documentos y agregamos el nombre del sitio a la lista
                for (document in documents) {
                    val sitCooX = document.getDouble("sitCooX") ?: 0.0
                    val sitCooY = document.getDouble("sitCooY") ?: 0.0
                    val nombre = document.getString("sitNom") ?: "Sitio"

                    addMarker(GeoPoint(sitCooX, sitCooY), nombre)
                    sitiosGeo.add(Pair(GeoPoint(sitCooX, sitCooY), nombre))
                    Log.d("sitios", "Sitios turísticos: $sitCooX $sitCooY $nombre")
                }
            }
            .addOnFailureListener { exception ->
                // Mostrar un Toast con el error si la carga falla
                exception.printStackTrace()
            }
        simulateUserMovement()
    }

    private fun addMarker(geoPoint: GeoPoint, title: String) {
        val marker = Marker(mapView)
        marker.position = geoPoint
        marker.title = title
        marker.icon = ContextCompat.getDrawable(requireContext(), R.drawable.icon_marker) // Asegúrate de tener un drawable para el marcador
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM) // Ancla el marcador en la parte inferior
        mapView.overlays.add(marker)
        mapView.invalidate() // Actualiza el mapa
    }


    fun calcularDistancia(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val radioTierra = 6371e3 // Radio de la Tierra en metros
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return radioTierra * c // Distancia en metros
    }

    private fun checkGPSAndLoadLocation() {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (!hasRedirectedToSettings) {
                hasRedirectedToSettings = true // Marcar que ya se redirigió
                // Mostrar diálogo antes de redirigir
                AlertDialog.Builder(requireContext())
                    .setTitle("Habilitar GPS")
                    .setMessage("Para usar esta funcionalidad, necesitas habilitar el GPS. ¿Quieres habilitarlo?")
                    .setPositiveButton("Sí") { _, _ ->
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(intent)
                    }
                    .setNegativeButton("No") { _, _ ->
                        Toast.makeText(requireContext(), "No se puede cargar la ubicación sin GPS", Toast.LENGTH_SHORT).show()
                    }
                    .show()
            }
        } else {
            hasRedirectedToSettings = false // Reinicia la bandera cuando el GPS esté habilitado
            loadCurrentLocation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Detener actualizaciones de ubicación al destruir el fragmento
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }

}
