package com.techteam.aqproad.Home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.techteam.aqproad.Item.ItemFragment
import com.techteam.aqproad.Login.FragmentLogin
import com.techteam.aqproad.R

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var edificaciones: MutableList<Edificacion>
    private lateinit var rootView: View
    private lateinit var btnListar: MaterialButton
    private lateinit var btnMasVisitados: MaterialButton
    private lateinit var btnCercanos: MaterialButton

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

        btnCercanos.setOnClickListener{

        }

        recyclerView.adapter = adapter
        setupButtonToggle()
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
                        imgUrl = info["sitImgEnl"] as? String ?: ""
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

}



