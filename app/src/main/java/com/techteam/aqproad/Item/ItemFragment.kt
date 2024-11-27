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
import android.widget.Button
import android.widget.EditText
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
import com.techteam.aqproad.Home.Comentario

class ItemFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var view: View
    private lateinit var carouselRecyclerView: RecyclerView
    private lateinit var recyView_comentarios: RecyclerView
    private lateinit var viewModel_comentarios: ComentarioViewModel
    private lateinit var adapter_comentarios: ComentarioAdapter
    private lateinit var editTextTextMultiLine: EditText  // Campo de texto para el comentario

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        view = inflater.inflate(R.layout.fragment_item, container, false)

        // Referencias a los elementos UI
        val btnExpand: ImageButton = view.findViewById(R.id.btn_expand)
        val ratingBar: RatingBar = view.findViewById(R.id.ratingCalif)
        editTextTextMultiLine = view.findViewById<EditText>(R.id.editTextTextMultiLine)  // EditText para comentarios

        // Arranque del carrusel
        setupCarouselRecyclerView()

        // Configuración del RecyclerView para comentarios
        recyView_comentarios = view.findViewById(R.id.recyView_comentarios)
        recyView_comentarios.layoutManager = LinearLayoutManager(requireContext())

        val repository = ComentarioRepository()
        viewModel_comentarios = ComentarioViewModel(repository)
        viewModel_comentarios.comentarios.observe(viewLifecycleOwner, Observer { comentarios ->
            adapter_comentarios = ComentarioAdapter(comentarios)
            recyView_comentarios.adapter = adapter_comentarios
        })

        viewModel_comentarios.loadComentarios()

        // Lógica para el botón de expansión (abre otro fragmento)
        btnExpand.setOnClickListener {
            val fragment = CroquisFragment()
            val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.main_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // Lógica para el botón de enviar comentario
        val btnSendComment: Button = view.findViewById<Button>(R.id.button_send_comment)
        btnSendComment.setOnClickListener {
            val commentText = editTextTextMultiLine.text.toString().trim()

            if (commentText.isNotEmpty()) {
                val usuarioActual = FirebaseAuth.getInstance().currentUser
                val userName = usuarioActual?.displayName ?: "Usuario"
                val newComment = Comentario(
                    id = (viewModel_comentarios.comentarios.value?.size ?: 0) + 1,  // Generar un id secuencial para el comentario
                    autor = userName.toString(),
                    contenido = commentText
                )

                // Agregar el nuevo comentario a la lista
                viewModel_comentarios.addComentario(newComment)

                // Limpiar el campo de texto después de enviar el comentario
                editTextTextMultiLine.text.clear()

                // Mostrar un mensaje de éxito
                showToast("Comentario enviado", view)
            } else {
                showToast("Por favor escribe un comentario", view)
            }
        }

        // Configuración de la RatingBar y permisos para calificar
        var isResettingRating = false
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            if (isResettingRating) return@setOnRatingBarChangeListener

            if (isUserLoggedIn()) {
                isUserAtLocation { isAtLocation ->
                    if (!isAtLocation) {
                        showToast("Debe estar en la ubicación de la edificación para calificar.", view)
                        isResettingRating = true
                        ratingBar.rating = 0f
                        isResettingRating = false
                        return@isUserAtLocation
                    } else {
                        showToast("Puedes calificar.", view)
                        saveUserRating(rating, view)
                    }
                }
            } else {
                showToast("Debe iniciar sesión para calificar.", view)
                isResettingRating = true
                ratingBar.rating = 0f
                isResettingRating = false
                return@setOnRatingBarChangeListener
            }
        }

        return view
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

    private fun showToast(message: String, view: View) {
        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()
    }

    private fun saveUserRating(rating: Float, view: View) {

        showToast("Gracias por calificar con $rating estrellas.", view)
    }

    private fun isUserLoggedIn(): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser != null
    }

    private fun isUserAtLocation(onResult: (Boolean) -> Unit) {
        val targetLatitude = -16.3911262
        val targetLongitude = -71.5122978

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
            showToast("No tiene permisos", view)
            onResult(false)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity(), OnSuccessListener<Location> { location ->
            if (location != null) {
                val userLatitude = location.latitude
                val userLongitude = location.longitude
                val distance = FloatArray(1)
                Location.distanceBetween(userLatitude, userLongitude, targetLatitude, targetLongitude, distance)
                val tolerance = 60.0f
                onResult(distance[0] <= tolerance)
            } else {
                showToast("No puede obtener la ubicación", view)
                onResult(false)
            }
        })
    }
}

