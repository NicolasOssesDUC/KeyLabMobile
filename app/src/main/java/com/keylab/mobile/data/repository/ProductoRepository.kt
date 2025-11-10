package com.keylab.mobile.data.repository

import com.keylab.mobile.data.local.ProductoDao
import com.keylab.mobile.data.remote.ApiResponse
import com.keylab.mobile.data.remote.SupabaseApiService
import com.keylab.mobile.domain.model.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Repository Pattern: Mediador entre ViewModels y fuentes de datos
 * 
 * Estrategia OFFLINE-FIRST:
 * 1. Devuelve datos de Room (local) inmediatamente
 * 2. Intenta sincronizar con Supabase (remoto) en background
 * 3. Actualiza Room con nuevos datos → UI se actualiza automáticamente (Flow)
 */
class ProductoRepository(
    private val dao: ProductoDao,
    private val api: SupabaseApiService
) {
    
    // ═══ LECTURA (Local first) ═══
    
    // Obtener todos desde Room (reactivo)
    fun obtenerProductos(): Flow<List<Producto>> = dao.obtenerTodos()
    
    // Buscar en Room
    fun buscarProductos(query: String): Flow<List<Producto>> = 
        dao.buscarPorNombre(query)
    
    // Filtrar por categoría en Room
    fun filtrarPorCategoria(categoria: String): Flow<List<Producto>> = 
        dao.obtenerPorCategoria(categoria)
    
    // Obtener por ID desde Room
    suspend fun obtenerProductoPorId(id: Int): Producto? = 
        withContext(Dispatchers.IO) {
            dao.obtenerPorId(id)
        }
    
    // ═══ SINCRONIZACIÓN (Room + API) ═══
    
    /**
     * Sincroniza productos desde Supabase → Room
     * Devuelve Flow con estados: Loading → Success/Error
     */
    fun sincronizarProductos(): Flow<ApiResponse<List<Producto>>> = flow {
        emit(ApiResponse.Loading)
        
        try {
            android.util.Log.d("ProductoRepository", "Iniciando sincronización...")
            
            // Request a Supabase
            val response = api.obtenerProductos()
            android.util.Log.d("ProductoRepository", "Response code: ${response.code()}")
            
            if (response.isSuccessful) {
                val productos = response.body() ?: emptyList()
                android.util.Log.d("ProductoRepository", "Productos recibidos: ${productos.size}")
                
                // Guardar en Room (reemplaza si existen)
                dao.insertarTodos(productos)
                android.util.Log.d("ProductoRepository", "Productos guardados en Room")
                
                emit(ApiResponse.Success(productos))
            } else {
                val error = "Error ${response.code()}: ${response.message()}"
                android.util.Log.e("ProductoRepository", error)
                emit(ApiResponse.Error(error))
            }
        } catch (e: Exception) {
            android.util.Log.e("ProductoRepository", "Exception: ${e.message}", e)
            emit(ApiResponse.Error(e.message ?: "Error desconocido"))
        }
    }.flowOn(Dispatchers.IO)
    
    // ═══ CREAR ═══
    
    /**
     * Crea producto en Supabase → Guarda en Room
     */
    suspend fun crearProducto(producto: Producto): ApiResponse<Producto> = 
        withContext(Dispatchers.IO) {
            try {
                val response = api.crearProducto(producto)
                
                if (response.isSuccessful) {
                    val productoCreado = response.body()?.firstOrNull()
                    
                    if (productoCreado != null) {
                        dao.insertar(productoCreado)
                        ApiResponse.Success(productoCreado)
                    } else {
                        ApiResponse.Error("Respuesta vacía del servidor")
                    }
                } else {
                    ApiResponse.Error("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                ApiResponse.Error(e.message ?: "Error al crear producto")
            }
        }
    
    // ═══ ACTUALIZAR ═══
    
    /**
     * Actualiza producto en Supabase → Room
     */
    suspend fun actualizarProducto(producto: Producto): ApiResponse<Producto> = 
        withContext(Dispatchers.IO) {
            try {
                val response = api.actualizarProducto("eq.${producto.id}", producto)
                
                if (response.isSuccessful) {
                    val productoActualizado = response.body()?.firstOrNull()
                    
                    if (productoActualizado != null) {
                        dao.actualizar(productoActualizado)
                        ApiResponse.Success(productoActualizado)
                    } else {
                        ApiResponse.Error("Respuesta vacía del servidor")
                    }
                } else {
                    ApiResponse.Error("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                ApiResponse.Error(e.message ?: "Error al actualizar producto")
            }
        }
    
    // ═══ ELIMINAR ═══
    
    /**
     * Elimina producto de Supabase → Room
     */
    suspend fun eliminarProducto(id: Int): ApiResponse<Unit> = 
        withContext(Dispatchers.IO) {
            try {
                val response = api.eliminarProducto("eq.$id")
                
                if (response.isSuccessful) {
                    dao.eliminarPorId(id)
                    ApiResponse.Success(Unit)
                } else {
                    ApiResponse.Error("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                ApiResponse.Error(e.message ?: "Error al eliminar producto")
            }
        }
}
