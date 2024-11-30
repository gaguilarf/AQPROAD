package com.techteam.aqproad.Home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.techteam.aqproad.Item.LaCompaniaView
import com.techteam.aqproad.R
import com.techteam.aqproad.SitioActivity

class EdificacionAdapter(
    private val edificaciones: MutableList<Edificacion>,
    private val onLikeClick: (Int) -> Unit // Callback para el clic en el botón "me gusta"
) : RecyclerView.Adapter<EdificacionAdapter.EdificacionViewHolder>() {

    inner class EdificacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.txtTitle)
        val locationText: TextView = itemView.findViewById(R.id.txtCat)
        val ratingText: TextView = itemView.findViewById(R.id.txtDes)
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
                    // Abrir LaCompaniaView cuando se hace clic en el ítem
                    val intent = Intent(itemView.context, SitioActivity::class.java)
                    intent.putExtra("SITIO_ID", edificaciones[position].id)
                    itemView.context.startActivity(intent)
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
        holder.titleText.text = edificacion.titulo
        holder.locationText.text = edificacion.ubicacion
        holder.ratingText.text = edificacion.calificacion

        // Actualizar el estado del "me gusta"
        val likeResId = if (edificacion.liked) R.drawable.icon_heart_filled else R.drawable.icon_heart
        holder.likeButton.setImageResource(likeResId)
    }

    override fun getItemCount(): Int = edificaciones.size
}
