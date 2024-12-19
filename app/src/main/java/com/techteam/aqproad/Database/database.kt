package com.techteam.aqproad.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.techteam.aqproad.Database.dao.HabitacionDAO
import com.techteam.aqproad.Database.dao.PinturaDAO
import com.techteam.aqproad.Database.dao.SitioDAO
import com.techteam.aqproad.Database.model.HabitacionModel
import com.techteam.aqproad.Database.model.PinturaModel
import com.techteam.aqproad.Database.model.SitioModel

@Database(
    entities = [
        SitioModel::class,
        HabitacionModel::class,
        PinturaModel::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sitioDAO(): SitioDAO
    abstract fun habitacionDAO(): HabitacionDAO
    abstract fun pinturaDAO(): PinturaDAO

    companion object {
        private var database: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (database == null) {
                database = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return database!!
        }
    }
}
