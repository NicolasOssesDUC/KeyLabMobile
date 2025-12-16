package com.keylab.mobile.data.repository

import com.keylab.mobile.data.local.ProductoDao
import com.keylab.mobile.data.remote.ApiResponse
import com.keylab.mobile.data.remote.SupabaseApiService
import com.keylab.mobile.domain.model.Producto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
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
                var productos = response.body() ?: emptyList()
                android.util.Log.d("ProductoRepository", "Productos recibidos: ${productos.size}")
                
                // Procesar URLs de imágenes
                productos = productos.map { producto ->
                    if (!producto.imagenUrl.isNullOrEmpty() && !producto.imagenUrl.startsWith("http")) {
                        val fullUrl = "${com.keylab.mobile.BuildConfig.SUPABASE_URL}/storage/v1/object/public/productos/${producto.imagenUrl}"
                        producto.copy(imagenUrl = fullUrl)
                    } else {
                        producto
                    }
                }

                // Log de los primeros productos para debugging
                productos.take(3).forEach { producto ->
                    android.util.Log.d("ProductoRepository", "Producto: id=${producto.id}, nombre=${producto.nombre}, img=${producto.imagenUrl}")
                }
                
                // Guardar en Room (reemplaza si existen)
                dao.insertarTodos(productos)
                android.util.Log.d("ProductoRepository", "Productos guardados en Room")
                
                emit(ApiResponse.Success(productos))
            } else {
                val errorBody = response.errorBody()?.string() ?: "Sin detalles"
                val error = "Error ${response.code()}: ${response.message()} - $errorBody"
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
                // Crear mapa excluyendo ID (para que Supabase lo genere)
                val productoMap = mapOf(
                    "nombre" to producto.nombre,
                    "precio" to producto.precio,
                    "categoria" to producto.categoria,
                    "subcategoria" to producto.subcategoria,
                    "descripcion" to producto.descripcion,
                    "stock" to producto.stock,
                    "imagen_url" to producto.imagenUrl
                )

                val response = api.crearProducto(productoMap)
                
                if (response.isSuccessful) {
                    val productoCreado = response.body()?.firstOrNull()
                    
                    if (productoCreado != null) {
                        dao.insertar(productoCreado)
                        ApiResponse.Success(productoCreado)
                    } else {
                        ApiResponse.Error("Respuesta vacía del servidor")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    ApiResponse.Error("Error ${response.code()}: ${response.message()} - $errorBody")
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
                // Crear mapa con solo los campos modificables (excluyendo ID y timestamps)
                val productoMap = mapOf(
                    "nombre" to producto.nombre,
                    "precio" to producto.precio,
                    "categoria" to producto.categoria,
                    "subcategoria" to producto.subcategoria,
                    "descripcion" to producto.descripcion,
                    "stock" to producto.stock,
                    "imagen_url" to producto.imagenUrl
                )

                val response = api.actualizarProducto("eq.${producto.id}", productoMap)
                
                if (response.isSuccessful) {
                    val productoActualizado = response.body()?.firstOrNull()
                    
                    if (productoActualizado != null) {
                        dao.actualizar(productoActualizado)
                        ApiResponse.Success(productoActualizado)
                    } else {
                        ApiResponse.Error("Respuesta vacía del servidor")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    ApiResponse.Error("Error ${response.code()}: ${response.message()} - $errorBody")
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

    // ═══ STORAGE ═══

    suspend fun subirImagen(file: java.io.File): ApiResponse<String> =
        withContext(Dispatchers.IO) {
            try {
                val requestFile = okhttp3.RequestBody.create(
                    "image/*".toMediaTypeOrNull(),
                    file
                )
                val body = okhttp3.MultipartBody.Part.createFormData("file", file.name, requestFile)
                
                // URL manual para Storage: /storage/v1/object/{bucket}/{filename}
                // Usamos bucket "productos"
                val fileName = "img_${System.currentTimeMillis()}_${file.name}"
                val bucket = "productos"
                val url = "${com.keylab.mobile.BuildConfig.SUPABASE_URL}/storage/v1/object/$bucket/$fileName"
                
                val response = api.subirImagen(url, body)
                
                if (response.isSuccessful) {
                    // Construir URL pública
                    val publicUrl = "${com.keylab.mobile.BuildConfig.SUPABASE_URL}/storage/v1/object/public/$bucket/$fileName"
                    ApiResponse.Success(publicUrl)
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    ApiResponse.Error("Error subida ${response.code()}: $errorBody")
                }
            } catch (e: Exception) {
                ApiResponse.Error(e.message ?: "Error al subir imagen")
            }
        }


}
