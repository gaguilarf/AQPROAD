package com.techteam.aqproad.Item.itemDB

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class RatingManagerDB {
    private val db = FirebaseFirestore.getInstance()

    fun saveRating(buildingID: Int, rating: Float, usuarioActual: String, callback: (String) -> Unit) {
        getBuildingDocument(buildingID) { docId ->
            if (docId != null) {
                saveUserRating(docId, usuarioActual, rating, callback)
            } else {
                callback("No se ha encontrado ninguna edificación con el id enviado")
            }
        }
    }

    fun getActualRatingBuild(buildingID: Int, callback: (String?, Double?) -> Unit) {
        getBuildingDocument(buildingID) { docId ->
            if (docId != null) {
                getRating(docId,
                    onSuccess = { puntaje ->
                        callback(null, puntaje)
                    },
                    onFailure = { mensaje ->
                        callback(mensaje, null)
                    })
            } else {
                callback("No se ha encontrado ninguna edificación con el id enviado", null)
            }
        }
    }

    private fun getRating(docId: String, onSuccess:(Double) -> Unit, onFailure:(String)->Unit) {
        db.collection("Sitios_turisticos").document(docId).get()
            .addOnSuccessListener { document ->
                if (document.exists() && document.contains("sitPun")) {
                    val puntaje = document.getDouble("sitPun")
                    if (puntaje != null)
                        onSuccess(puntaje)
                    else
                        onFailure("El puntaje no se encontro dado el id del sitio")

                } else {
                    onFailure("No existe el docuemnto o no contiene la propiedad sitPun")
                }
            }
            .addOnFailureListener {
                onFailure("No se pudo obtener el documento respectivo al ID del sitio ${it.message}")
            }
    }

    private fun getBuildingDocument(buildingID: Int, callback: (String?) -> Unit) {
        db.collection("Sitios_turisticos")
            .whereEqualTo("sitID", buildingID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val docId = querySnapshot.documents[0].id
                    callback(docId)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                Log.d("RATING MANAGER", "Ocurrre un error al buscar el sitio por el id pasado getBuildingDocument, ${e.message}")
                callback(null)
            }
    }

    private fun saveUserRating(docId: String, usuarioActual: String, rating: Float, callback: (String) -> Unit) {
        val colCalificacion = db.collection("Sitios_turisticos")
            .document(docId)
            .collection("Calificacion")

        colCalificacion.document(usuarioActual).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    updateUserRating(colCalificacion, usuarioActual, rating, callback)
                } else {
                    createUserRating(colCalificacion, usuarioActual, rating, callback)
                }
                //update build rating
                updateBuildRating(docId, callback)
                Log.d("DENTRO DE RATING MANAGER DB", "Se ha actualizado el rating de la edificacion")
            }
            .addOnFailureListener { e ->
                callback("Error durante la búsqueda del documento en la colección anidada: ${e.message}")
            }
    }

    private fun updateBuildRating(docId: String, callback: (String) -> Unit) {
        val colCalificacion = db.collection("Sitios_turisticos")
            .document(docId)
            .collection("Calificacion")

        colCalificacion.get().addOnSuccessListener { querySnapshot ->
            var total = 0.0
            var count = 0

            for (document in querySnapshot) {
                val calificacion = document.getDouble("usuCal")
                if (calificacion != null) {
                    total += calificacion
                    count++
                }
            }

            if (count > 0) {
                val promedio = total / count
                val edificacion = db.collection("Sitios_turisticos").document(docId)
                edificacion.update("sitPun", promedio)
                    .addOnSuccessListener { callback("Actualización exitosa de la edificación ${promedio}") }
                    .addOnFailureListener {callback("Error al actualizar le edificacion")}
            }

        }.addOnFailureListener { exception ->
            callback("Hubo un fallo al obtener la lista de las calificaciones del sitio pasado")
        }

    }

    private fun updateUserRating(
        colCalificacion: CollectionReference,
        usuarioActual: String,
        rating: Float,
        callback: (String) -> Unit
    ) {
        colCalificacion.document(usuarioActual)
            .update("usuCal", rating)
            .addOnSuccessListener {
                callback("Calificación actualizada exitosamente")
            }
            .addOnFailureListener {
                callback("Error al actualizar la calificación")
            }
    }

    private fun createUserRating(
        colCalificacion: CollectionReference,
        usuarioActual: String,
        rating: Float,
        callback: (String) -> Unit
    ) {
        val data = hashMapOf(
            "usuCal" to rating
        )
        colCalificacion.document(usuarioActual)
            .set(data)
            .addOnSuccessListener {
                callback("Nueva calificación registrada exitosamente")
            }
            .addOnFailureListener {
                callback("Error al crear la nueva calificación")
            }
    }
}