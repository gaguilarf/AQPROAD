package com.techteam.aqproad.Item

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.techteam.aqproad.R
import com.bumptech.glide.Glide
import com.example.recyclerview.ComentarioAdapter
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.techteam.aqproad.Home.ComentarioViewModel
import com.techteam.aqproad.Home.ComentarioRepository
import androidx.lifecycle.Observer

class ItemFragment : Fragment() {
    private lateinit var fusedLocationClient:FusedLocationProviderClient
    private lateinit var view: View
    private lateinit var carouselRecyclerView: RecyclerView

    private lateinit var recyView_comentarios: RecyclerView
    private lateinit var viewModel_comentarios: ComentarioViewModel
    private lateinit var adapter_comentarios: ComentarioAdapter

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private fun setupCarouselRecyclerView() {
        carouselRecyclerView = view.findViewById(R.id.recyclerCarousel)
        CarouselSnapHelper().attachToRecyclerView(carouselRecyclerView)
        carouselRecyclerView.adapter = CarouselAdapter(images = getImages())
    }

    private fun getImages() : List<String> {
        return listOf(
            "https://d3cjd3eir1atrn.cloudfront.net/jesusCautivo.jpg",
            "https://d3cjd3eir1atrn.cloudfront.net/jesusNazareth.jpg")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        view = inflater.inflate(R.layout.fragment_item, container, false)

        val btnExpand: ImageButton = view.findViewById(R.id.btn_expand)
        val ratingBar: RatingBar = view.findViewById(R.id.ratingCalif)

        // Arranque de carrusel
        setupCarouselRecyclerView()

        btnExpand.setOnClickListener {
            val fragment = CroquisFragment()
            val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.main_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            // Manejar el valor de la calificación
            val permite = isUserLoggedIn() // si está registrado
            if (permite) {
                isUserAtLocation { isAtLocation ->
                    if (!isAtLocation) {
                        showToast("Debe estar en la ubicación de la edificación para calificar.", view)
                        ratingBar.rating = 0f
                        return@isUserAtLocation
                    } else {
                        // El usuario está en la ubicación, puedes proceder
                        showToast("Puedes calificar.", view)
                    }
                }
            } else {
                showToast("Debe iniciar sesión para calificar.", view)
                ratingBar.rating = 0f
                return@setOnRatingBarChangeListener
            }
            /*
            if (!isUserLoggedIn()) {
                showToast("Debe iniciar sesión para calificar.", view)
                ratingBar.rating = 0f
                return@setOnRatingBarChangeListener
            }
            isUserAtLocation { isAtLocation ->
                if (permite && !isAtLocation) {
                    showToast("Debe estar en la ubicación de la edificación para calificar.", view)
                    ratingBar.rating = 0f
                    return@isUserAtLocation
                } else {
                    // El usuario está en la ubicación, puedes proceder
                    showToast("Puedes calificar.", view)
                }
            }*/
            saveUserRating(rating, view)
        }


        // INICIO Comentarios de las edificaciones

        recyView_comentarios = view.findViewById(R.id.recyView_comentarios)

        recyView_comentarios.layoutManager = LinearLayoutManager(requireContext())

        val repository = ComentarioRepository()
        viewModel_comentarios = ComentarioViewModel(repository)
        viewModel_comentarios.comentarios.observe(viewLifecycleOwner, Observer { comentarios ->
            adapter_comentarios = ComentarioAdapter(comentarios)
            recyView_comentarios.adapter = adapter_comentarios
        })

        viewModel_comentarios.loadEdificaciones()

        // FIN Comentarios de las edificaciones

        return view
    }



    private fun saveUserRating(rating: Float, view: View) {
        showToast("Gracias por calificar con $rating estrellas.", view)
        // Guardar la calificación en la base de datos o realizar otra acción
    }
    private fun showToast(message: String, view: View) {
        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()
    }

    private fun isUserLoggedIn(): Boolean {
        // Verificar si hay un usuario logeado, por ejemplo, usando SharedPreferences o tu sistema de autenticación.
        //val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        //return sharedPreferences.getBoolean("is_logged_in", false)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            return true
        }
        return false
    }

    private fun isUserAtLocation(onResult: (Boolean) -> Unit) {
        // Coordenadas del lugar de la edificación
        val targetLatitude = -16.3911262
        val targetLongitude = -71.5122978

        // Verificar permisos de ubicación
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si no se tienen permisos, puedes solicitar los permisos aquí
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
            showToast("No tiene permisos", view)
            onResult(false)
            return
        }

        // Obtener la ubicación actual
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity(), OnSuccessListener<Location> { location ->
            if (location != null) {
                val userLatitude = location.latitude
                val userLongitude = location.longitude

                // Verificar si el usuario está en la ubicación deseada
                val distance = FloatArray(1)
                Location.distanceBetween(userLatitude, userLongitude, targetLatitude, targetLongitude, distance)

                // Considera un rango de tolerancia (ejemplo: 50 metros)
                val tolerance = 60.0f
                showToast("Sí lee ubi", view)
                Log.d("TAGAAAA", distance[0].toString())
                onResult(distance[0] <= tolerance)
            } else {
                // Si no se puede obtener la ubicación
                showToast("No puede obtener la ubicación", view)
                onResult(false)
            }
        })
    }
}
