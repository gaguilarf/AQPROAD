package com.techteam.aqproad.Home

import android.util.Log
import com.techteam.aqproad.Item.itemDB.CommentManagerDB

class ComentarioRepository() {

    private var comentarios : MutableList<Comentario> = mutableListOf()
    private val commentManagerDB = CommentManagerDB()

    /*fun getComentarios(sitId: Int): List<Comentario> {
        commentManagerDB.getCommentsBySite(sitId) {comentarioList, msg ->
            if(comentarioList == null){
                Log.d("ComentariosRepository", "COMENTARIOS VACIO")
                comentarios = mutableListOf()
            }else{
                comentarios = comentarioList as MutableList<Comentario>
                Log.d("ComentariosRepository", "COMENTARIOS COUNT: " + comentarios.size)
            }

        }

        Log.d("ComentariosRepository", "COMENTARIOS COUNT PRERETURN: " + comentarios.size)
        for (comentario in comentarios){
            Log.d("ComentariosRepository", "COMENTARIOS: " + comentario.autor)
        }
        return comentarios
    }*/

    fun getComentarios(sitId: Int, callback: (List<Comentario>) -> Unit) {
        commentManagerDB.getCommentsBySite(sitId) { comentarioList, msg ->
            if (comentarioList == null) {
                Log.d("ComentariosRepository", "COMENTARIOS VACIO")
                callback(emptyList()) // Devuelve una lista vacía si no hay datos
            } else {
                Log.d("ComentariosRepository", "COMENTARIOS COUNT: ${comentarioList.size}")
                callback(comentarioList) // Devuelve los datos cuando están listos
            }
        }
    }

    fun saveComment(
        sitId: Int,
        comUsuNom: String,
        comDet: String,
        comFec: String,
        callback: (String) -> Unit
    ) {
        commentManagerDB.saveComment(sitId, comUsuNom, comDet, comFec, callback)
    }
}