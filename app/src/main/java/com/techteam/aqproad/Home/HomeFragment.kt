package com.techteam.aqproad.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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



        return rootView
    }

    private fun getEdificaciones(): List<Edificacion> {
        return listOf(
            Edificacion(1, "https://www.example.com/image.jpg", "El Mirador Yanahuara", "Yanahuara, Arequipa", "4.8", false),
            Edificacion(2, "https://www.example.com/image2.jpg", "Plaza de Armas", "Arequipa, Perú", "4.5", true),
            Edificacion(3, "https://www.example.com/image3.jpg", "Monasterio de Santa Catalina", "Arequipa, Perú", "5.0", false)
        )
    }
}



