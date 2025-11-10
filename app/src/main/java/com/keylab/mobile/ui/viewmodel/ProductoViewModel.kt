package com.keylab.mobile.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keylab.mobile.data.remote.ApiResponse
import com.keylab.mobile.data.repository.ProductoRepository
import com.keylab.mobile.domain.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestión de productos
 * 
 * Responsabilidades:
 * - Exponer datos a la UI (Flow/LiveData)
 * - Manejar estados (loading, errores)
 * - Ejecutar operaciones CRUD mediante Repository
 * - Sobrevive a rotaciones de pantalla
 */
class ProductoViewModel(
    private val repository: ProductoRepository
) : ViewModel() {
    
    // ═══ OBSERVABLES PARA UI (Flow desde Room) ═══
    
    // Lista de productos (reactivo: se actualiza automáticamente)
    val productos: Flow<List<Producto>> = repository.obtenerProductos()
    
    // ═══ ESTADOS (LiveData) ═══
    
    // Indica si hay operación en progreso
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    // Mensaje de error (null = sin error)
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    // Mensaje de éxito (para confirmaciones)
    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage
    
    // ═══ OPERACIONES DE LECTURA ═══
    
    /**
     * Sincroniza productos desde Supabase → Room
     * UI recibe actualizaciones automáticas vía Flow
     */
    fun sincronizarProductos() {
        viewModelScope.launch {
            repository.sincronizarProductos().collect { response ->
                when (response) {
                    is ApiResponse.Loading -> {
                        _isLoading.value = true
                        _error.value = null
                    }
                    is ApiResponse.Success -> {
                        _isLoading.value = false
                        _successMessage.value = "Productos sincronizados: ${response.data.size}"
                    }
                    is ApiResponse.Error -> {
                        _isLoading.value = false
                        _error.value = response.message
                    }
                }
            }
        }
    }
    
    /**
     * Buscar productos por nombre
     * Retorna Flow que se actualiza automáticamente
     */
    fun buscarProductos(query: String): Flow<List<Producto>> {
        return repository.buscarProductos(query)
    }
    
    /**
     * Filtrar por categoría
     */
    fun filtrarPorCategoria(categoria: String): Flow<List<Producto>> {
        return repository.filtrarPorCategoria(categoria)
    }
    
    /**
     * Obtener producto por ID (suspend = para navegación a detalle)
     */
    suspend fun obtenerProductoPorId(id: Int): Producto? {
        return repository.obtenerProductoPorId(id)
    }
    
    // ═══ OPERACIONES DE ESCRITURA ═══
    
    /**
     * Crear nuevo producto en Supabase → Room
     */
    fun crearProducto(producto: Producto) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            when (val result = repository.crearProducto(producto)) {
                is ApiResponse.Success -> {
                    _isLoading.value = false
                    _successMessage.value = "Producto creado: ${result.data.nombre}"
                }
                is ApiResponse.Error -> {
                    _isLoading.value = false
                    _error.value = result.message
                }
                else -> { /* Loading handled above */ }
            }
        }
    }
    
    /**
     * Actualizar producto existente
     */
    fun actualizarProducto(producto: Producto) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            when (val result = repository.actualizarProducto(producto)) {
                is ApiResponse.Success -> {
                    _isLoading.value = false
                    _successMessage.value = "Producto actualizado"
                }
                is ApiResponse.Error -> {
                    _isLoading.value = false
                    _error.value = result.message
                }
                else -> { /* Loading handled above */ }
            }
        }
    }
    
    /**
     * Eliminar producto
     */
    fun eliminarProducto(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            when (val result = repository.eliminarProducto(id)) {
                is ApiResponse.Success -> {
                    _isLoading.value = false
                    _successMessage.value = "Producto eliminado"
                }
                is ApiResponse.Error -> {
                    _isLoading.value = false
                    _error.value = result.message
                }
                else -> { /* Loading handled above */ }
            }
        }
    }
    
    // ═══ UTILIDADES ═══
    
    /**
     * Limpiar mensaje de error (después de mostrarlo)
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Limpiar mensaje de éxito (después de mostrarlo)
     */
    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}
