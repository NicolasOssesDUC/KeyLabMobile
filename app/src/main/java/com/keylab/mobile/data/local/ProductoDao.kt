package com.keylab.mobile.data.local

import androidx.room.*
import com.keylab.mobile.domain.model.Producto
import kotlinx.coroutines.flow.Flow

/**
 * DAO: Operaciones CRUD para tabla productos
 * Flow = Reactivo (emite cambios automáticos)
 * suspend = Ejecuta en background
 */
@Dao
interface ProductoDao {
    
    // ═══ LECTURA (SELECT) ═══
    
    // Todos los productos (más recientes primero)
    @Query("SELECT * FROM productos ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<Producto>>
    
    // Por ID
    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Producto?
    
    // Búsqueda por nombre (parcial, case-insensitive)
    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :busqueda || '%' COLLATE NOCASE ORDER BY nombre")
    fun buscarPorNombre(busqueda: String): Flow<List<Producto>>
    
    // Filtrar por categoría
    @Query("SELECT * FROM productos WHERE categoria = :categoria ORDER BY nombre")
    fun obtenerPorCategoria(categoria: String): Flow<List<Producto>>
    
    // Solo con stock disponible
    @Query("SELECT * FROM productos WHERE stock > 0 ORDER BY stock DESC")
    fun obtenerConStock(): Flow<List<Producto>>
    
    // ═══ ESCRITURA (INSERT/UPDATE/DELETE) ═══
    
    // Insertar uno (REPLACE si existe)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(producto: Producto)
    
    // Insertar múltiples (para sincronización masiva)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(productos: List<Producto>)
    
    // Actualizar producto existente
    @Update
    suspend fun actualizar(producto: Producto)
    
    // Eliminar producto
    @Delete
    suspend fun eliminar(producto: Producto)
    
    // Eliminar por ID
    @Query("DELETE FROM productos WHERE id = :id")
    suspend fun eliminarPorId(id: Int)
    
    // Limpiar toda la tabla
    @Query("DELETE FROM productos")
    suspend fun eliminarTodos()
    
    // ═══ ESTADÍSTICAS ═══
    
    // Contar total
    @Query("SELECT COUNT(*) FROM productos")
    suspend fun contarProductos(): Int
    
    // Contar por categoría
    @Query("SELECT COUNT(*) FROM productos WHERE categoria = :categoria")
    suspend fun contarPorCategoria(categoria: String): Int
}
