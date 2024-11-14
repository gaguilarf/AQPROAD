package com.techteam.aqproad.Login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.FragmentTransaction
import com.techteam.aqproad.Item.CroquisFragment
import com.techteam.aqproad.R


class WelcomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_welcome, container, false)

        val buttonContinue = view.findViewById<Button>(R.id.button_continue)

        buttonContinue.setOnClickListener {
            val loginFragment = FragmentLogin()

            parentFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainer, loginFragment)

                addToBackStack(null)

                commit()
            }
        }
        return view
    }

}