package com.techteam.aqproad.Home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.techteam.aqproad.R
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var edificaciones: MutableList<Edificacion>
    private lateinit var rootView: View
    private lateinit var btnListar: MaterialButton
    private lateinit var btnMasVisitados: MaterialButton
    private lateinit var btnCercanos: MaterialButton
    private lateinit var imgUser: ImageView
    private lateinit var btnSearch: ImageButton
    private lateinit var edtWord: TextInputEditText
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    private lateinit var toggleGroup: MaterialButtonToggleGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_home, container, false)

        val txtUserName = rootView.findViewById<TextView>(R.id.txtHomeUsername)
        val usuarioActual = FirebaseAuth.getInstance().currentUser
        val userName = usuarioActual?.displayName ?: "Usuario"
        txtUserName.text = "Hola, $userName!"
        btnListar = rootView.findViewById(R.id.btnListar)
        btnMasVisitados = rootView.findViewById(R.id.btnMasVisitados)
        btnCercanos = rootView.findViewById(R.id.button_cercanos)
        recyclerView = rootView.findViewById(R.id.list_places)
        imgUser = rootView.findViewById(R.id.imgUser)
        btnSearch = rootView.findViewById(R.id.btnSearch)
        edtWord = rootView.findViewById(R.id.edtWord)
        toggleGroup = rootView.findViewById(R.id.btnToggleGroup)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        imgUser.setOnClickListener {
            val userFragment = UserFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.main_container, userFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        edificaciones = getEdificaciones()

        val adapter = EdificacionAdapter(edificaciones) {
        }

        val adapter2 = EdificacionAdapterTwo(edificaciones) {
        }

        btnMasVisitados.setOnClickListener {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }

        btnListar.setOnClickListener {
            recyclerView.adapter = adapter2
            recyclerView.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
        }

        btnCercanos.setOnClickListener {
            checkLocationPermissionAndGetLocation()
        }

        recyclerView.adapter = adapter
        setupButtonToggle()

        btnSearch.setOnClickListener {
            val word = edtWord.text.toString()
            val edificacionesFiltradas = edificaciones.filter { it.sitNom.contains(word, ignoreCase = true) }

            when (toggleGroup.checkedButtonId) {
                R.id.btnListar -> {
                    val adapter = EdificacionAdapterTwo(edificacionesFiltradas) {
                    }
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                }
                else -> {
                    val adapter = EdificacionAdapter(edificacionesFiltradas) {
                    }
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )

                }
            }

        }

        return rootView
    }

    private fun getEdificaciones(): MutableList<Edificacion> {
        val db = FirebaseFirestore.getInstance()
        val edificacionesList = mutableListOf<Edificacion>()

        db.collection("Sitios_turisticos")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val info = document.data
                    val edificacion = Edificacion(
                        sitId = info["sitId"] as? Int ?: 0,
                        sitCooX = info["sitCooX"] as? Float ?: 0f,
                        sitCooY = info["sitCooY"] as? Float ?: 0f,
                        sitDes = info["sitDes"] as? String ?: "",
                        sitNom = info["sitNom"] as? String ?: "",
                        sitPun = info["sitPun"] as? Float ?: 0f,
                        imgUrl = info["sitImgEnl"] as? String ?: "",
                        sitCro = info["sitCro"] as? Boolean ?: false
                    )
                    edificacionesList.add(edificacion)
                }
                recyclerView.adapter?.notifyDataSetChanged()

            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting documents: ", exception)
            }

        return edificacionesList
    }

    fun setupButtonToggle() {
        val toggleGroup = rootView.findViewById<MaterialButtonToggleGroup>(R.id.btnToggleGroup)
        val buttonMasVisitados = rootView.findViewById<MaterialButton>(R.id.btnMasVisitados)
        val buttonCercanos = rootView.findViewById<MaterialButton>(R.id.button_cercanos)
        val buttonRecientes = rootView.findViewById<MaterialButton>(R.id.btnListar)

        // Agregar listener para el cambio de estado de los botones
        toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnMasVisitados -> {
                        buttonMasVisitados.setBackgroundColor(ContextCompat.getColor(rootView.context, R.color.orange))
                        buttonCercanos.setBackgroundColor(ContextCompat.getColor(rootView.context, R.color.gris))
                        buttonRecientes.setBackgroundColor(ContextCompat.getColor(rootView.context, R.color.gris))
                    }
                    R.id.button_cercanos -> {
                        buttonMasVisitados.setBackgroundColor(ContextCompat.getColor(rootView.context, R.color.gris))
                        buttonCercanos.setBackgroundColor(ContextCompat.getColor(rootView.context, R.color.orange))
                        buttonRecientes.setBackgroundColor(ContextCompat.getColor(rootView.context, R.color.gris))
                    }
                    R.id.btnListar -> {
                        buttonMasVisitados.setBackgroundColor(ContextCompat.getColor(rootView.context, R.color.gris))
                        buttonCercanos.setBackgroundColor(ContextCompat.getColor(rootView.context, R.color.gris))
                        buttonRecientes.setBackgroundColor(ContextCompat.getColor(rootView.context, R.color.orange))
                    }
                }
            }
        }

        // Establecer el primer botón como seleccionado por defecto
        toggleGroup.check(R.id.btnMasVisitados)
    }

    private fun checkLocationPermissionAndGetLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permiso de ubicación denegado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val userLat = it.latitude
                    val userLon = it.longitude
                    sortEdificacionesByDistance(userLat, userLon)

                } ?: run{
                    Toast.makeText(
                        requireContext(),
                        "No se pudo obtener la ubicación",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Permiso de ubicación no otorgado",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun sortEdificacionesByDistance(userLat: Double, userLon: Double) {
        val sortedEdificaciones = edificaciones.sortedBy { edificacion ->
            val edificacionLat = edificacion.sitCooY.toDouble()
            val edificacionLon = edificacion.sitCooX.toDouble()
            calcularDistancia(userLat, userLon, edificacionLat, edificacionLon)
        }

        when (toggleGroup.checkedButtonId) {
            R.id.btnListar -> {
                val adapter = EdificacionAdapterTwo(sortedEdificaciones) {
                }
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )
            }
            else -> {
                val adapter = EdificacionAdapter(sortedEdificaciones) {
                }
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                )

            }
        }
    }

    private fun calcularDistancia(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val radioTierra = 6371e3 // Radio de la Tierra en metros
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return radioTierra * c // Distancia en metros
    }

}