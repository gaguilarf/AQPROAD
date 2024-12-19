package com.techteam.aqproad.Item

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.techteam.aqproad.R

class RoomFragment : Fragment() {

    private lateinit var room: Room
    private lateinit var roomView: RoomView
    private lateinit var imageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle = arguments
        if (bundle != null) {
            room = bundle.getParcelable("room") ?: return null
        }

        val rootView = inflater.inflate(R.layout.fragment_room, container, false)

        // Accede al RoomView desde el XML
        roomView = rootView.findViewById(R.id.roomView)

        // Accede al ImageView desde el XML
        imageView = rootView.findViewById(R.id.image)

        // Establece los datos de la habitación
        roomView.setRoom(room)

        // Carga una imagen en el ImageView usando Glide
        loadRoomImage()

        return rootView
    }

    private fun loadRoomImage() {
        val imageUrl = "https://raw.githubusercontent.com/rvelizs/imagenesAQPROAD/refs/heads/main/1731643126931nat.jpg" // Asegúrate de que `Room` tenga una propiedad `imageUrl`
        Log.d("RoomFragment", "Loading image from $imageUrl")
        Glide.with(this)
            .load(imageUrl)
            .into(imageView)
    }
}

