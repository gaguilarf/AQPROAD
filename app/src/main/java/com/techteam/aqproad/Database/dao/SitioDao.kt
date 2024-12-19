package com.techteam.aqproad.Database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.techteam.aqproad.Database.model.SitioModel

@Dao
interface SitioDAO {

    @Query("SELECT * FROM Sitio")
    suspend fun getAllSitios(): List<SitioModel>

    @Insert
    suspend fun insertSitio(sitio: SitioModel)

    @Update
    suspend fun updateSitio(sitio: SitioModel)

    @Delete
    suspend fun deleteSitio(sitio: SitioModel)

    @Query("SELECT * FROM Sitio WHERE sit_id = :sitioId")
    suspend fun getSitioById(sitioId: Long): SitioModel?

    @Query("SELECT * FROM Sitio WHERE nombre LIKE :nombre")
    suspend fun searchSitiosByName(nombre: String): List<SitioModel>
}
