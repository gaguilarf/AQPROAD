package com.techteam.aqproad.Home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.techteam.aqproad.Item.ItemFragment
import com.techteam.aqproad.Login.FragmentLogin
import com.techteam.aqproad.R

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var edificaciones: MutableList<Edificacion>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        val txtUserName = rootView.findViewById<TextView>(R.id.txtHomeUsername)
        val usuarioActual = FirebaseAuth.getInstance().currentUser
        val userName = usuarioActual?.displayName ?: "Usuario"
        txtUserName.text = "Hola, ${userName.toString()}!"

        recyclerView = rootView.findViewById(R.id.list_places)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        edificaciones = getEdificaciones()

        val adapter = EdificacionAdapter(edificaciones) { position ->
            val edificacion = edificaciones[position]
            //edificacion.liked = !edificacion.liked
        }

        recyclerView.adapter = adapter

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
                        sitPun = info["sitPun"] as? Float ?: 0f
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
}



