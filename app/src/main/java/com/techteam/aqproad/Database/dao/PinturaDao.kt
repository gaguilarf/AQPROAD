package com.techteam.aqproad.Database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.techteam.aqproad.Database.model.PinturaModel

@Dao
interface PinturaDAO {

    @Query("SELECT * FROM Pintura WHERE pint_id = :pinturaId")
    suspend fun getPinturaById(pinturaId: Long): PinturaModel?

    @Query("SELECT * FROM Pintura WHERE hab_id = :habitacionId")
    suspend fun getPinturasByHabitacion(habitacionId: Long): List<PinturaModel>

    @Insert
    suspend fun insertPintura(pintura: PinturaModel)

    @Update
    suspend fun updatePintura(pintura: PinturaModel)

    @Delete
    suspend fun deletePintura(pintura: PinturaModel)
}
