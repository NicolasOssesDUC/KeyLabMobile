package com.keylab.mobile.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.keylab.mobile.domain.model.CarritoItem
import com.keylab.mobile.domain.model.Producto

/**
 * Database principal de Room (SQLite local)
 * entities: Lista de tablas (@Entity)
 */
@Database(
    entities = [Producto::class, CarritoItem::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun productoDao(): ProductoDao
    abstract fun carritoDao(): CarritoDao
    
    companion object {
        // Singleton: Solo una instancia de la BD
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * Obtiene instancia única de la BD
         * Si no existe, la crea; si existe, la reutiliza
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "keylab_database"
                )
                    .fallbackToDestructiveMigration() // Recrear BD si cambia schema
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
