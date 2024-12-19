package com.techteam.aqproad.Database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.techteam.aqproad.Database.model.HabitacionModel

@Dao
interface HabitacionDAO {

    @Query("SELECT * FROM Habitacion WHERE sitio_id = :sitioId")
    suspend fun getHabitacionesBySitio(sitioId: Long): List<HabitacionModel>

    @Insert
    suspend fun insertHabitacion(habitacion: HabitacionModel)

    @Update
    suspend fun updateHabitacion(habitacion: HabitacionModel)

    @Delete
    suspend fun deleteHabitacion(habitacion: HabitacionModel)
}
