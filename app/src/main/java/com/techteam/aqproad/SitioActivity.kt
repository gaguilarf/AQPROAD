package com.techteam.aqproad

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.techteam.aqproad.Item.ItemFragment

class SitioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sitio)
        /*enableEdgeToEdge()
        setContentView(R.layout.activity_sitio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        if (savedInstanceState == null) {
            val sitioId = intent.getIntExtra("SITIO_ID", 1)

            val itemFragment = ItemFragment.newInstance(sitioId)
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, itemFragment)
                .addToBackStack(null)
                .commit()
        }
    }
}