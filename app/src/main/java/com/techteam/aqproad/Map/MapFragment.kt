package com.techteam.aqproad.Map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
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
import android.util.Log
import androidx.fragment.app.Fragment
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mapView: MapView
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        val view = binding.root

        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        mapView = binding.mapFragmentView
        mapView.setMultiTouchControls(true)

        db = FirebaseFirestore.getInstance()

        // Verificar permisos de ubicación
        context?.let {
            if (ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                loadCurrentLocation()
            }
        }

        // Cargar sitios turísticos desde Firestore
        loadTouristSites()

        return view
    }

    private fun loadCurrentLocation() {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location: Location? = if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        } else {
            null
        }

        if (location != null) {
            val userLocation = GeoPoint(location.latitude, location.longitude)
            mapView.controller.setZoom(19.0) // Nivel de zoom cuando se encuentra la ubicación actual
            mapView.controller.setCenter(userLocation)
        } else {
            // Opción: mantener el centro por defecto si no hay datos de ubicación
            val defaultLocation = GeoPoint(-16.3988625, -71.5368597) // Coordenadas de Arequipa
            mapView.controller.setCenter(defaultLocation)
            mapView.controller.setZoom(19.0)
        }
    }

    private fun loadTouristSites() {
        db.collection("Sitios_turisticos")
            .orderBy("sitID") // Ordenar por sitID
            .get()
            .addOnSuccessListener { documents ->
                // Creamos una lista para almacenar los nombres de los sitios turísticos
                val sitios = mutableListOf<String>()

                // Iteramos sobre los documentos y agregamos el nombre del sitio a la lista
                for (document in documents) {
                    val sitCooX = document.getDouble("sitCooX") ?: 0.0
                    val sitCooY = document.getDouble("sitCooY") ?: 0.0
                    val nombre = document.getString("sitNom") ?: "Sitio"
                    sitios.add(nombre)

                    addMarker(GeoPoint(sitCooX, sitCooY + 0.0026), nombre)
                    Log.d("coor", "Coordenadas: lat: $sitCooX, lon: $sitCooY")
                }
            }
            .addOnFailureListener { exception ->
                // Mostrar un Toast con el error si la carga falla
                exception.printStackTrace()
            }
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
}