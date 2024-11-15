package com.techteam.aqproad.Login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.techteam.aqproad.R
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class FragmentRegister : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = (activity as LoginActivity).auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val txtLogin = view.findViewById<TextView>(R.id.txtLogin)
        txtLogin.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        setUp(view)
    }

    private fun setUp(view: View) {
        val btnContinue = view.findViewById<Button>(R.id.btnContinue)
        btnContinue.setOnClickListener{
            val name = view.findViewById<EditText>(R.id.inputName)
            val mail = view.findViewById<EditText>(R.id.inputMail)
            val username = view.findViewById<EditText>(R.id.inputUser)
            val pass1 = view.findViewById<EditText>(R.id.inputPassword)
            val pass2 = view.findViewById<EditText>(R.id.inputVerifyPassword)

            if (name.text.isNotEmpty() && mail.text.isNotEmpty() && username.text.isNotEmpty() && pass1.text.isNotEmpty() && pass2.text.isNotEmpty() && (pass1.text.toString() == pass2.text.toString())) {
                val email = mail.text.toString()
                val password = pass1.text.toString()
                Log.d("TAAAAAAAAAAAAAAAAAAAG", "EMAIL: " + email + "\nPASS: " + password)

                isUsernameTaken(username.text.toString(),
                    onSuccess = { userNameTaken ->
                        if(userNameTaken) {
                            Toast.makeText(view.context, "Ya existe este nombre de ususario, pruebe otro", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            createUserWithEmailPass(email, password,
                                onSuccess = { userFirebase ->
                                    userFirebase.email?.let { it1 -> Log.d("EMAIL", it1.toString()) }
                                    userFirebase.uid.let { it2 -> Log.d("UUID", it2.toString())}

                                    saveNewUser(name.text.toString(), mail.text.toString(), username.text.toString()) { sucess, error ->
                                        if (sucess) {
                                            parentFragmentManager.popBackStack()
                                            Toast.makeText(view.context, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
                                        }
                                        else {
                                            Toast.makeText(view.context, "Error al guardar usuario: $error", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                onFailure = { error ->
                                    Toast.makeText(view.context, error, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                    onFailure = {
                        Toast.makeText(view.context, "Ha ocurrido un error inesperado al obtener el userName de la BD", Toast.LENGTH_SHORT).show()
                    }
                )
            }
            else {
                Toast.makeText(view.context, "Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun isUsernameTaken(
        userName: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: () -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Usuario").document(userName).get()
            .addOnSuccessListener { document ->
                onSuccess(document.exists())
            }
            .addOnFailureListener { exception ->
                Log.d("REGISTER", "Error al obtener el documento", exception)
                onFailure()
            }
    }

    private fun createUserWithEmailPass(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.let {
                        it -> onSuccess(it)
                    }
                }
                else {
                    onFailure(task.exception?.message ?: "Error al crear iniciar autenticacion")
                }
            }
    }

    private fun saveNewUser(
        nombreReal: String,
        correo: String,
        nombreUsuario: String,
        callback: (Boolean, String?) -> Unit
    ) {
        db.collection("Usuario").document(nombreUsuario).set(
            hashMapOf(
                "email" to correo,
                "name" to nombreReal,
                "photoUrl" to ""
            )
        ).addOnSuccessListener {
            callback(true, null) //exito
        }.addOnFailureListener {exception ->
            callback(false, exception.message) //error pipipi
        }
    }
}