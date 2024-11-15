package com.techteam.aqproad.Login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.techteam.aqproad.MainActivity
import com.techteam.aqproad.R

class FragmentLogin : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var viewGlobal: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = (activity as LoginActivity).auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewGlobal = view
        val txtRegister = view.findViewById<TextView>(R.id.txtRegister)
        txtRegister.setOnClickListener{
            val fragmetRegister = FragmentRegister()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragmetRegister)
                .addToBackStack(null)
                .commit()
        }
        setUp(view)
    }

    private fun setUp(view: View) {
        val btnLoginNormal = view.findViewById<Button>(R.id.btnLoginNormal)
        btnLoginNormal.setOnClickListener{
            val userName = view.findViewById<EditText>(R.id.inputUserLogin)
            val pass = view.findViewById<EditText>(R.id.inputPasswordLogin)

            if (fieldsNotEmpty(userName, pass)) {
                val username = userName.text.toString()
                val password = pass.text.toString()

                signInWithUsername(username, password)
            }
            else {
                Toast.makeText(view.context, "Llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fieldsNotEmpty(vararg fields: EditText): Boolean{
        return fields.all { it.text.isNotEmpty() }
    }

    private fun signInWithUsername(username: String, password: String) {

        getEmailByUsername(username,
            onSuccess = {email ->
                signInWithEmail(email, password,
                    onSuccess = { user ->
                        Log.d("LOGIN", "SIGNIN Exitoso")
                        updatePerfil(user, username)
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    },
                    onFailure = { error ->
                        Toast.makeText(viewGlobal.context, error, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            onFailure = { error ->
                Toast.makeText(viewGlobal.context, error, Toast.LENGTH_SHORT).show()
            }
        )

    }

    private fun getEmailByUsername(
        username: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Usuario").document(username).get()
            .addOnSuccessListener {document ->
                if (document.exists() && document.contains("email")) {
                    val email = document.getString("email")
                    if (email != null) {
                        onSuccess(email)
                    }
                    else {
                        onFailure("El correo no se encontró en el documento")
                    }
                }
            }
            .addOnFailureListener {
                onFailure("Error al consultar Firestore: ${it.message}")
            }
    }

    private fun signInWithEmail(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.let {
                        it -> onSuccess(it)
                    }
                } else {
                    onFailure(task.exception?.message ?: "Error desconocido al iniciar sesión")
                }
            }
    }

    private fun updatePerfil(user: FirebaseUser, username: String){
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(username)
            .build()

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FRAGMENT LOGIN", "Se ha seteado el username al objeto currentUser")
                    Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                }
                else {
                    Log.d("FRAGMENT LOGIN", "Error al guardar el username al objeto currentUser")
                    Toast.makeText(context, "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
                }
            }
    }

}