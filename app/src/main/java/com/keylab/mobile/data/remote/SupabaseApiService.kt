package com.keylab.mobile.data.remote

import com.keylab.mobile.domain.model.Producto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface Retrofit para API REST de Supabase
 * 
 * Headers requeridos:
 * - apikey: Clave anon de Supabase
 * - Authorization: Bearer token
 * - Content-Type: application/json
 * - Prefer: return=representation (devuelve objeto creado/actualizado)
 */
interface SupabaseApiService {
    
    // ═══ GET - Lectura ═══
    
    // Obtener todos los productos
    @GET("productos")
    suspend fun obtenerProductos(
        @Query("select") select: String = "*"
    ): Response<List<Producto>>
    
    // Obtener producto por ID (usa filtro eq)
    @GET("productos")
    suspend fun obtenerProductoPorId(
        @Query("id") id: String, // "eq.{id}"
        @Query("select") select: String = "*"
    ): Response<List<Producto>>
    
    // Filtrar por categoría
    @GET("productos")
    suspend fun obtenerPorCategoria(
        @Query("categoria") categoria: String, // "eq.Teclados"
        @Query("select") select: String = "*"
    ): Response<List<Producto>>
    
    // Buscar por nombre (like parcial)
    @GET("productos")
    suspend fun buscarPorNombre(
        @Query("nombre") nombre: String, // "like.*{texto}*"
        @Query("select") select: String = "*"
    ): Response<List<Producto>>
    
    // ═══ POST - Crear ═══
    
    // Crear producto (devuelve producto creado con ID)
    @Headers("Prefer: return=representation")
    @POST("productos")
    suspend fun crearProducto(
        @Body producto: Any // Aceptamos Map o Producto
    ): Response<List<Producto>>
    
    // ═══ PATCH - Actualizar ═══
    
    // Actualizar producto por ID
    @Headers("Prefer: return=representation")
    @PATCH("productos")
    suspend fun actualizarProducto(
        @Query("id") id: String, // "eq.{id}"
        @Body producto: Any // Aceptamos Map o Producto parcial
    ): Response<List<Producto>>
    
    // ═══ DELETE - Eliminar ═══
    
    // Eliminar producto por ID
    @DELETE("productos")
    suspend fun eliminarProducto(
        @Query("id") id: String // "eq.{id}"
    ): Response<Unit>

    // ═══ AUTH ═══

    @POST
    suspend fun signInWithIdToken(
        @Url url: String,
        @Query("grant_type") grantType: String = "id_token",
        @Body request: SupabaseAuthRequest
    ): Response<SupabaseAuthResponse>

    // ═══ STORAGE - Subir Imagen ═══
    @Multipart
    @POST
    suspend fun subirImagen(
        @Url url: String,
        @Part file: MultipartBody.Part,
        @Header("x-upsert") upsert: String = "true"
    ): Response<Unit>
}
