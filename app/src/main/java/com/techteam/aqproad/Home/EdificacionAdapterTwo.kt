package com.techteam.aqproad.Home

import android.os.Bundle
import android.util.Log
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

class EdificacionAdapterTwo(
    private val edificaciones: List<Edificacion>,
    private val onLikeClick: (Int) -> Unit // Callback para el clic en el botón "me gusta"
) : RecyclerView.Adapter<EdificacionAdapterTwo.EdificacionViewHolder>() {

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
                Log.d("qwerty", "ID SITIO: ${position}")
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_placetwo, parent, false)
        return EdificacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: EdificacionViewHolder, position: Int) {
        val edificacion = edificaciones[position]

        // Asignar los datos al item
        holder.titleText.text = edificacion.sitNom
        holder.locationText.text = edificacion.sitNom
        holder.ratingText.text = edificacion.sitPun.toString()

        val imageResource = getImageResourceForId(position+1)
        holder.imgBack.setImageResource(imageResource)

    }

    private fun getImageResourceForId(sitId: Int): Int {
        return when (sitId) {
            1 -> R.raw.museo_santuarios_andinos
            2 -> R.raw.plaza_armas_arequipa
            3 -> R.raw.museo_arte_virreinal
            4 -> R.raw.plaza_san_francisco
            5 -> R.raw.parque_libertad_expresion
            6 -> R.raw.iglesia
            7 -> R.raw.casona_santa_catalina
            8 -> R.raw.teatro_municipal
            9 -> R.raw.mirador_yanahuara
            10 -> R.raw.monasterio_santa_catalina
            // Agrega más condiciones si es necesario
            else -> R.raw.museo_santuarios_andinos // Imagen predeterminada si no coincide
        }
    }


    override fun getItemCount(): Int = edificaciones.size
}
