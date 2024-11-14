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
import com.techteam.aqproad.MainActivity
import com.techteam.aqproad.R

class FragmentLogin : Fragment() {

    private lateinit var auth: FirebaseAuth
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
            val user = view.findViewById<EditText>(R.id.inputUserLogin)
            val pass = view.findViewById<EditText>(R.id.inputPasswordLogin)

            if (user.text.isNotEmpty() && pass.text.isNotEmpty()) {
                val email = user.text.toString()
                val password = user.text.toString()

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("LOGIN", "SIGNIN Exitoso")
                            val user = auth.currentUser
                            Log.d("LOGIN", user.toString())
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        } else {
                            Log.w("LOGIN", "SIGNING FALLO", task.exception)
                            Toast.makeText(
                                view.context,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            }
            else {
                Toast.makeText(
                    view.context,
                    "Llena todos los campos",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }
}