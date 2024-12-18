package com.techteam.aqproad.Home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.techteam.aqproad.Login.LoginActivity
import com.techteam.aqproad.R

class UserFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var btnLogout: MaterialButton
    private lateinit var edtUser: EditText
    private lateinit var edtCorreo: EditText
    private lateinit var btnEdtUser: MaterialButton
    private lateinit var btnSave: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_user, container, false)
        auth = FirebaseAuth.getInstance()
        btnLogout = rootView.findViewById(R.id.btnLogout)
        edtUser = rootView.findViewById(R.id.edtBdUser)
        edtCorreo = rootView.findViewById(R.id.edtBdCorreo)
        btnEdtUser = rootView.findViewById(R.id.btnEdtUser)
        btnSave = rootView.findViewById(R.id.btnSave)
        val usuarioActual = FirebaseAuth.getInstance().currentUser
        val userName = usuarioActual?.displayName ?: "Usuario"
        val userCorreo = usuarioActual?.email ?: "Correo"
        edtUser.setText(userName)
        edtCorreo.setText(userCorreo)

        btnEdtUser.setOnClickListener {
            edtUser.focusable = View.FOCUSABLE
            edtUser.isFocusableInTouchMode = true
            edtUser.isClickable = true
            edtUser.background = resources.getDrawable(android.R.drawable.edit_text, null)

            edtUser.requestFocus()
            btnEdtUser.visibility = View.INVISIBLE
            btnSave.visibility = View.VISIBLE

        }


        btnSave.setOnClickListener {
            edtUser.focusable = View.NOT_FOCUSABLE
            edtUser.isFocusableInTouchMode = false
            edtUser.isClickable = false
            edtUser.background = resources.getDrawable(R.drawable.edittext_line_white, null)

            btnEdtUser.visibility = View.VISIBLE
            btnSave.visibility = View.INVISIBLE

            // Actualizar el nombre de usuario
            val newDisplayName = edtUser.text.toString()
            val user = FirebaseAuth.getInstance().currentUser

            user?.let {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(newDisplayName)
                    .build()

                it.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("UpdateUser", "Nombre de usuario actualizado correctamente")

                            // Actualizar el token para reflejar los nuevos datos
                            it.reload().addOnCompleteListener { reloadTask ->
                                if (reloadTask.isSuccessful) {
                                    Log.d("UpdateUser", "Token de usuario recargado correctamente")
                                    Log.d("UpdatedDisplayName", it.displayName ?: "No disponible")
                                } else {
                                    Log.e("UpdateUser", "Error al recargar el token", reloadTask.exception)
                                }
                            }
                        } else {
                            Log.e("UpdateUser", "Error al actualizar nombre de usuario", task.exception)
                        }
                    }
            }

        }


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