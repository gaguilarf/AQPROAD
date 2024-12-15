package com.techteam.aqproad.Home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.techteam.aqproad.Login.LoginActivity
import com.techteam.aqproad.R

class UserFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var btnLogout: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_user, container, false)
        auth = FirebaseAuth.getInstance()
        btnLogout = rootView.findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            logout()
        }
        return rootView
    }

    private fun logout() {
        auth.signOut()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // Finaliza la actividad actual para evitar volver atrás
    }

}