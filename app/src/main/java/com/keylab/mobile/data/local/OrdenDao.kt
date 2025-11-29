package com.keylab.mobile.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.keylab.mobile.domain.model.Orden
import com.keylab.mobile.domain.model.OrdenItem
import kotlinx.coroutines.flow.Flow

@Dao
interface OrdenDao {
    
    @Insert
    suspend fun insertarOrden(orden: Orden): Long
    
    @Insert
    suspend fun insertarOrdenItems(items: List<OrdenItem>)
    
    @Query("SELECT * FROM ordenes WHERE usuario_id = :usuarioId ORDER BY fecha_orden DESC")
    fun obtenerOrdenesPorUsuario(usuarioId: String): Flow<List<Orden>>
    
    @Query("SELECT * FROM ordenes WHERE id = :ordenId LIMIT 1")
    suspend fun obtenerOrdenPorId(ordenId: Int): Orden?
    
    @Query("SELECT * FROM orden_items WHERE orden_id = :ordenId")
    suspend fun obtenerItemsPorOrden(ordenId: Int): List<OrdenItem>
    
    @Query("SELECT COUNT(*) FROM ordenes WHERE usuario_id = :usuarioId")
    fun contarOrdenesPorUsuario(usuarioId: String): Flow<Int>
}
