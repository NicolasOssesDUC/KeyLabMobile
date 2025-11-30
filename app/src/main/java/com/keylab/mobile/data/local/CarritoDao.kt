package com.keylab.mobile.data.local

import androidx.room.*
import com.keylab.mobile.domain.model.CarritoItem
import kotlinx.coroutines.flow.Flow

/**
 * DAO para el carrito de compras
 */
@Dao
interface CarritoDao {
    
    // Obtener todos los items del carrito (reactivo)
    @Query("SELECT * FROM carrito_items ORDER BY fechaAgregado DESC")
    fun obtenerItems(): Flow<List<CarritoItem>>
    
    // Obtener un item específico
    @Query("SELECT * FROM carrito_items WHERE productoId = :productoId")
    suspend fun obtenerPorProductoId(productoId: Int): CarritoItem?
    
    // Insertar o actualizar item
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(item: CarritoItem)
    
    // Actualizar cantidad
    @Query("UPDATE carrito_items SET cantidad = :cantidad WHERE productoId = :productoId")
    suspend fun actualizarCantidad(productoId: Int, cantidad: Int)
    
    // Eliminar item
    @Delete
    suspend fun eliminar(item: CarritoItem)
    
    // Eliminar por ID de producto
    @Query("DELETE FROM carrito_items WHERE productoId = :productoId")
    suspend fun eliminarPorId(productoId: Int)
    
    // Vaciar carrito
    @Query("DELETE FROM carrito_items")
    suspend fun vaciarCarrito()
    
    // Obtener total de items
    @Query("SELECT COUNT(*) FROM carrito_items")
    fun contarItems(): Flow<Int>
    
    // Obtener subtotal
    @Query("SELECT SUM(precio * cantidad) FROM carrito_items")
    fun obtenerSubtotal(): Flow<Double?>
}
