package com.techteam.aqproad.Database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Sitio")
data class SitioModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sit_id")
    val sitioId: Long = 0L,

    @ColumnInfo(name = "nombre")
    val nombre: String
)
