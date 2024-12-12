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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_home, container, false)

        val txtUserName = rootView.findViewById<TextView>(R.id.txtHomeUsername)
        val usuarioActual = FirebaseAuth.getInstance().currentUser
        val userName = usuarioActual?.displayName ?: "Usuario"
        txtUserName.text = "Hola, $userName!"

        recyclerView = rootView.findViewById(R.id.list_places)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        edificaciones = getEdificaciones()

        val adapter = EdificacionAdapter(edificaciones) { position ->
            val edificacion = edificaciones[position]
            //edificacion.liked = !edificacion.liked
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
        val toggleGroup = rootView.findViewById<MaterialButtonToggleGroup>(R.id.button_toggle_group)
        val buttonMasVisitados = rootView.findViewById<MaterialButton>(R.id.button_mas_visitados)
        val buttonCercanos = rootView.findViewById<MaterialButton>(R.id.button_cercanos)
        val buttonRecientes = rootView.findViewById<MaterialButton>(R.id.button_recientes)

        // Agregar listener para el cambio de estado de los botones
        toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.button_mas_visitados -> {
                        buttonMasVisitados.setBackgroundColor(ContextCompat.getColor(rootView.context, R.color.orange))
                        buttonCercanos.setBackgroundColor(ContextCompat.getColor(rootView.context, R.color.gris))
                        buttonRecientes.setBackgroundColor(ContextCompat.getColor(rootView.context, R.color.gris))
                    }
                    R.id.button_cercanos -> {
                        buttonMasVisitados.setBackgroundColor(ContextCompat.getColor(rootView.context, R.color.gris))
                        buttonCercanos.setBackgroundColor(ContextCompat.getColor(rootView.context, R.color.orange))
                        buttonRecientes.setBackgroundColor(ContextCompat.getColor(rootView.context, R.color.gris))
                    }
                    R.id.button_recientes -> {
                        buttonMasVisitados.setBackgroundColor(ContextCompat.getColor(rootView.context, R.color.gris))
                        buttonCercanos.setBackgroundColor(ContextCompat.getColor(rootView.context, R.color.gris))
                        buttonRecientes.setBackgroundColor(ContextCompat.getColor(rootView.context, R.color.orange))
                    }
                }
            }
        }

        // Establecer el primer botón como seleccionado por defecto
        toggleGroup.check(R.id.button_mas_visitados)
    }

}



