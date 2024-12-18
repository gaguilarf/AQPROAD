package com.techteam.aqproad

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseApp
import com.techteam.aqproad.Home.ChatbotFragment
import com.techteam.aqproad.Home.FavoritesFragment
import com.techteam.aqproad.Home.HomeFragment
import com.techteam.aqproad.Home.UserFragment
import com.techteam.aqproad.Map.MapFragment
import com.techteam.aqproad.databinding.ActivityMainBinding
import java.util.Locale

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
                R.id.chatbot_menu -> loadFragment(ChatbotFragment())
                R.id.user_menu -> loadFragment(UserFragment())
                else -> false
            }
            true
        }

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        val navigateTo = intent.getStringExtra("navigate_to")
        if (navigateTo == "MapFragment") {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MapFragment())
                .addToBackStack(null)
                .commit()
        }
        setLocale(this, "es")
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .commit()
    }

    private fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        context.createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

}