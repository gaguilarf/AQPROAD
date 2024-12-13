package com.techteam.aqproad.Item.itemDB
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.techteam.aqproad.Home.Comentario
class CommentManagerDB {
    private val db = FirebaseFirestore.getInstance()
    // Método para guardar un nuevo comentario
    fun saveComment(sitId: Int, comUsuNom: String, comDet: String, comFec: String, callback: (String) -> Unit) {
        val commentData = hashMapOf(
            "ComFec" to comFec,
            "ComDet" to comDet,
            "ComUsuNom" to comUsuNom,
            "sitId" to sitId
        )
        db.collection("Comentario")
            .add(commentData)
            .addOnSuccessListener { documentReference ->
                callback("Comentario guardado exitosamente con ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                callback("Error al guardar el comentario: ${e.message}")
            }
    }
    // Método para obtener comentarios de un sitio específico
    fun getCommentsBySite(sitId: Int, callback: (List<Comentario>?, String?) -> Unit) {
        db.collection("Comentario")
            .whereEqualTo("sitId", sitId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val comList = querySnapshot.documents.mapNotNull { document ->
                        val data = document.data
                        // Asegúrate de extraer correctamente los datos del comentario
                        data?.let {
                            Comentario(
                                id = it["id"] as? Int ?: 0,
                                autor = it["ComUsuNom"] as? String ?: "Desconocido",
                                contenido = it["ComDet"] as? String ?: ""
                            )
                        }
                    }
                    Log.d("COMMENT MANAGER", "Lista de comentarios de sitio:" + sitId)
                    for(Comentario in comList){
                        Log.d("COMMENT MANAGER", "1) " + Comentario.autor + ": " + Comentario.contenido + " |")
                    }
                    Log.d("COMMENT MANAGER", "FINAL Lista de comentarios de sitio:" + sitId)
                    callback(comList, null)
                } else {
                    callback(null, "No se encontraron comentarios para el sitio ID: $sitId")
                }
            }
            .addOnFailureListener { e ->
                Log.d("COMMENT MANAGER", "Error al obtener comentarios: ${e.message}")
                callback(null, "Error al obtener comentarios")
            }
    }
    // Método para actualizar un comentario existente
    fun updateComment(commentId: String, newContent: String, callback: (String) -> Unit) {
        db.collection("Comentario").document(commentId)
            .update("ComeDet", newContent)
            .addOnSuccessListener {
                callback("Comentario actualizado exitosamente")
            }
            .addOnFailureListener { e ->
                callback("Error al actualizar el comentario: ${e.message}")
            }
    }
    // Método para eliminar un comentario
    fun deleteComment(commentId: String, callback: (String) -> Unit) {
        db.collection("Comentario").document(commentId)
            .delete()
            .addOnSuccessListener {
                callback("Comentario eliminado exitosamente")
            }
            .addOnFailureListener { e ->
                callback("Error al eliminar el comentario: ${e.message}")
            }
    }
}