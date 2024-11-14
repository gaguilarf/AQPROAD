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
import com.google.firebase.auth.FirebaseAuth

class FragmentRegister : Fragment() {
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
            val user = view.findViewById<EditText>(R.id.inputUser)
            val pass1 = view.findViewById<EditText>(R.id.inputPassword)
            val pass2 = view.findViewById<EditText>(R.id.inputVerifyPassword)

            if (name.text.isNotEmpty() && mail.text.isNotEmpty() && user.text.isNotEmpty() && pass1.text.isNotEmpty() && pass2.text.isNotEmpty() && (pass1.text.toString() == pass2.text.toString())) {
                val email = mail.text.toString()
                val password = mail.text.toString()
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("REGISTER", "Usuario creado satisfactoriamnete")
                            val user = auth.currentUser
                            Log.d("TAG", user.toString())
                            parentFragmentManager.popBackStack()
                        }
                        else {
                            Log.w("REGISTER", "Fallo al crear el usuario", task.exception)
                            Toast.makeText(
                                view.context,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            }
            else {
                Toast.makeText(view.context, "Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
            }
        }
    }
}