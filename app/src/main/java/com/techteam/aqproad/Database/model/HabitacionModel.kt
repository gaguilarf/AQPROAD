package com.techteam.aqproad.Database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Habitacion",
    foreignKeys = [
        ForeignKey(
            entity = SitioModel::class,
            parentColumns = ["sit_id"],
            childColumns = ["sitio_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HabitacionModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "hab_id")
    val habitacionId: Long = 0L,

    @ColumnInfo(name = "left")
    val left: Float,

    @ColumnInfo(name = "top")
    val top: Float,

    @ColumnInfo(name = "right")
    val right: Float,

    @ColumnInfo(name = "bottom")
    val bottom: Float,

    @ColumnInfo(name = "sitio_id")
    val sitioId: Long
)
