package com.keylab.mobile.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.keylab.mobile.domain.model.Direccion
import kotlinx.coroutines.flow.Flow

@Dao
interface DireccionDao {
    
    @Query("SELECT * FROM direcciones WHERE usuario_id = :usuarioId ORDER BY id DESC")
    fun obtenerDireccionesPorUsuario(usuarioId: Int): Flow<List<Direccion>>
    
    @Insert
    suspend fun insertar(direccion: Direccion)
    
    @Update
    suspend fun actualizar(direccion: Direccion)
    
    @Delete
    suspend fun eliminar(direccion: Direccion)
    
    @Query("DELETE FROM direcciones WHERE id = :id")
    suspend fun eliminarPorId(id: Int)
}
