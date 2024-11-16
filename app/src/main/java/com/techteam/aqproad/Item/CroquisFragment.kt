package com.techteam.aqproad.Item

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.techteam.aqproad.R

class CroquisFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Forzar la orientación horizontal (landscape) cuando el fragmento es creado
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Inflar el layout del fragmento
        return inflater.inflate(R.layout.fragment_croquis, container, false)
    }

    // Para restaurar la orientación anterior cuando se abandone el Fragment
    override fun onDestroyView() {
        super.onDestroyView()
        // Restaurar la orientación de la actividad cuando el fragmento sea destruido
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}