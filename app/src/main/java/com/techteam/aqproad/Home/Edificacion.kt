package com.techteam.aqproad.Home

data class Edificacion(
    val id: Int,
    val imagenUrl: String,
    val titulo: String,
    val ubicacion: String,
    val calificacion: String,
    var liked: Boolean
)