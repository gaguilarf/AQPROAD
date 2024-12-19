package com.techteam.aqproad.Database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Pintura",
    foreignKeys = [
        ForeignKey(
            entity = HabitacionModel::class,
            parentColumns = ["hab_id"],
            childColumns = ["hab_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PinturaModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pint_id")
    val pinturaId: Long = 0L,

    @ColumnInfo(name = "x_pos")
    val xPos: Float,

    @ColumnInfo(name = "y_pos")
    val yPos: Float,

    @ColumnInfo(name = "descripcion")
    val descripcion: String,

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "titulo")
    val titulo: String,

    @ColumnInfo(name = "hab_id")
    val habitacionId: Long
)
