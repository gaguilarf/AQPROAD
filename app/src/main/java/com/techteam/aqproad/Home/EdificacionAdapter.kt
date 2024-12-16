package com.techteam.aqproad.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.techteam.aqproad.Item.ItemFragment
import com.techteam.aqproad.R

class EdificacionAdapter(
    private val edificaciones: List<Edificacion>,
    private val onLikeClick: (Int) -> Unit // Callback para el clic en el botón "me gusta"
) : RecyclerView.Adapter<EdificacionAdapter.EdificacionViewHolder>() {

    inner class EdificacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.txtTitle)
        val locationText: TextView = itemView.findViewById(R.id.txtCat)
        val ratingText: TextView = itemView.findViewById(R.id.txtDes)
        val imgBack: ImageView = itemView.findViewById(R.id.imgBackground)
        val likeButton: ImageButton = itemView.findViewById(R.id.button_heart)

        init {
            // Asignar la acción del "me gusta" cuando se hace clic en el botón
            likeButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onLikeClick(position) // Llamamos al callback para actualizar el estado
                }
            }

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val itemFragment = ItemFragment()

                    // Pasar datos al fragmento
                    val bundle = Bundle()
                    bundle.putString("title", edificaciones[position].sitNom)
                    bundle.putString("description", edificaciones[position].sitDes)
                    bundle.putInt("img", position+1)
                    bundle.putString("imgUrl", edificaciones[position].imgUrl)
                    itemFragment.arguments = bundle

                    // Realizar la transacción del fragmento
                    val fragmentManager = (itemView.context as AppCompatActivity).supportFragmentManager
                    fragmentManager.beginTransaction()
                        .replace(R.id.main_container, itemFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EdificacionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_place, parent, false)
        return EdificacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: EdificacionViewHolder, position: Int) {
        val edificacion = edificaciones[position]

        // Asignar los datos al item
        holder.titleText.text = edificacion.sitNom
        holder.locationText.text = edificacion.sitNom
        holder.ratingText.text = edificacion.sitPun.toString()

        // Establecer la imagen de fondo según el sitId
        val imageResource = getImageResourceForTitle(edificacion.sitNom)
        holder.imgBack.setImageResource(imageResource)
    }

    private fun getImageResourceForTitle(title: String): Int {
        return when (title.lowercase()) { // Convertir a minúsculas para evitar problemas de mayúsculas/minúsculas
            "museo santuarios andinos" -> R.raw.museo_santuarios_andinos
            "plaza de armas" -> R.raw.plaza_armas_arequipa
            "museo de arte virreinal" -> R.raw.museo_arte_virreinal
            "plaza de san francisco" -> R.raw.plaza_san_francisco
            "parque libertad" -> R.raw.parque_libertad_expresion
            "iglesia de la compañia" -> R.raw.iglesia
            "casona de santa catalina" -> R.raw.casona_santa_catalina
            "teatro municipal" -> R.raw.teatro_municipal
            "mirador de yanahuara" -> R.raw.mirador_yanahuara
            "monasterio de santa catalina" -> R.raw.monasterio_santa_catalina
            // Agrega más títulos según sea necesario
            else -> R.raw.museo_santuarios_andinos // Imagen predeterminada si no coincide
        }
    }



    override fun getItemCount(): Int = edificaciones.size
}
