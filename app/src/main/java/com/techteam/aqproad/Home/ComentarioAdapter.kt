package com.example.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.techteam.aqproad.Home.Comentario
import com.techteam.aqproad.R

class ComentarioAdapter(
    private var comentarios: List<Comentario>
) : RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder>() {

    fun updateData(newData: List<Comentario>) {
        this.comentarios = newData
        notifyDataSetChanged()
    }

    class ComentarioViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtv_autorcomentario: TextView = itemView.findViewById(R.id.txtv_autorcomentario)
        val txtv_comentario: TextView = itemView.findViewById(R.id.txtv_comentario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comentario, parent, false)
        return ComentarioViewHolder(view)
    }

    override fun getItemCount(): Int {
        return comentarios.size
    }

    override fun onBindViewHolder(holder: ComentarioViewHolder, position: Int) {
        val comentario = comentarios[position]
        holder.txtv_autorcomentario.text = comentario.autor
        holder.txtv_comentario.text = comentario.contenido

    }
}