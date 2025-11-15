package com.keylab.mobile.data.remote

/**
 * Sealed class para manejar estados de respuestas HTTP
 */
sealed class ApiResponse<out T> {
    // Éxito con datos
    data class Success<T>(val data: T) : ApiResponse<T>()
    
    // Error con mensaje
    data class Error(val message: String) : ApiResponse<Nothing>()
    
    // Cargando (para mostrar loading en UI)
    object Loading : ApiResponse<Nothing>()
}
