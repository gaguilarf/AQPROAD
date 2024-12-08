package com.techteam.aqproad

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.techteam.aqproad.Home.FavoritesFragment
import com.techteam.aqproad.Home.HomeFragment
import com.techteam.aqproad.Home.UserFragment
import com.techteam.aqproad.Map.MapFragment
import com.techteam.aqproad.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        val bottomNavigationView = binding.bottomNavView
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_menu -> loadFragment(HomeFragment())
                R.id.map_menu -> loadFragment(MapFragment())
                R.id.favorites_menu -> loadFragment(FavoritesFragment())
                R.id.user_menu -> loadFragment(UserFragment())
                else -> false
            }
            true
        }

        // Cargar fragmento inicial
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())  // Cargar fragmento inicial (Home)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .commit()
    }
}