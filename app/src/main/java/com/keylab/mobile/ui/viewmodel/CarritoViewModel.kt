package com.keylab.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keylab.mobile.data.repository.CarritoRepository
import com.keylab.mobile.domain.model.CarritoItem
import com.keylab.mobile.domain.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * ViewModel para el carrito de compras
 */
class CarritoViewModel(private val repository: CarritoRepository) : ViewModel() {
    
    // Items en el carrito
    val items: Flow<List<CarritoItem>> = repository.obtenerItems()
    
    // Cantidad total de items
    val totalItems: Flow<Int> = repository.contarItems()
    
    // Subtotal
    val subtotal: Flow<Double> = repository.obtenerSubtotal().map { it ?: 0.0 }
    
    // Costo de envío (gratis si > $50.000)
    val costoEnvio: Flow<Double> = subtotal.map { subtotal ->
        if (subtotal > 50000) 0.0 else 3990.0
    }
    
    // Total final
    val total: Flow<Double> = subtotal.map { subtotalValue ->
        val envio = if (subtotalValue > 50000) 0.0 else 3990.0
        subtotalValue + envio
    }
    
    /**
     * Agregar producto al carrito
     */
    fun agregarProducto(producto: Producto) {
        viewModelScope.launch {
            repository.agregarProducto(producto)
            android.util.Log.d("CarritoViewModel", "Producto agregado: ${producto.nombre}")
        }
    }
    
    /**
     * Actualizar cantidad de un item
     */
    fun actualizarCantidad(productoId: Int, cantidad: Int) {
        viewModelScope.launch {
            repository.actualizarCantidad(productoId, cantidad)
        }
    }
    
    /**
     * Incrementar cantidad de un item
     */
    fun incrementarCantidad(productoId: Int) {
        viewModelScope.launch {
            repository.incrementarCantidad(productoId)
        }
    }
    
    /**
     * Decrementar cantidad de un item
     */
    fun decrementarCantidad(productoId: Int) {
        viewModelScope.launch {
            repository.decrementarCantidad(productoId)
        }
    }
    
    /**
     * Eliminar item del carrito
     */
    fun eliminarItem(productoId: Int) {
        viewModelScope.launch {
            repository.eliminarItem(productoId)
        }
    }
    
    /**
     * Vaciar carrito
     */
    fun vaciarCarrito() {
        viewModelScope.launch {
            repository.vaciarCarrito()
        }
    }
}
