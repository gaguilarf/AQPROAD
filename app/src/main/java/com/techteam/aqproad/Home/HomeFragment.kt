package com.techteam.aqproad.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
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

        // Inicializar RecyclerView
        recyclerView = rootView.findViewById(R.id.list_places)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        edificaciones = getEdificaciones().toMutableList()

        val adapter = EdificacionAdapter(edificaciones) { position ->
            val edificacion = edificaciones[position]
            edificacion.liked = !edificacion.liked

        }

        // Asignar el adaptador al RecyclerView

        recyclerView.adapter = adapter

        val txtUserName = rootView.findViewById<TextView>(R.id.txtHomeUsername)
        val usuarioActual = FirebaseAuth.getInstance().currentUser
        val userName = usuarioActual?.displayName ?: "Usuario"
        txtUserName.text = "Hola, ${userName.toString()}!"

        return rootView
    }

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val txtUserName = view.findViewById<TextView>(R.id.txtHomeUsername)
        val usuarioActual = FirebaseAuth.getInstance().currentUser
        val userName = usuarioActual?.displayName ?: "Usuario"
        txtUserName.text = "Hola, ${userName.toString()}!"
    }*/

    private fun getEdificaciones(): List<Edificacion> { //aqui deeb ir un repository para deolver las edificaciones actuales
        return listOf(
            Edificacion(1, "https://www.example.com/image.jpg", "Iglesia de la compañia", "Arequipa, Arequipa", "4.8", false),
            Edificacion(2, "https://www.example.com/image2.jpg", "Plaza de Armas", "Arequipa, Perú", "4.5", true),
            Edificacion(3, "https://www.example.com/image3.jpg", "Monasterio de Santa Catalina", "Arequipa, Perú", "5.0", false)
        )
    }
}



