package com.keylab.mobile.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keylab.mobile.data.repository.CarritoRepository
import com.keylab.mobile.data.repository.OrdenRepository
import com.keylab.mobile.domain.model.CarritoItem
import com.keylab.mobile.domain.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * ViewModel para el carrito de compras y proceso de pago
 */
class CarritoViewModel(
    private val carritoRepository: CarritoRepository,
    private val ordenRepository: OrdenRepository
) : ViewModel() {
    
    // Items en el carrito
    val items: Flow<List<CarritoItem>> = carritoRepository.obtenerItems()
    
    // Cantidad total de items
    val totalItems: Flow<Int> = carritoRepository.contarItems()
    
    // Subtotal
    val subtotal: Flow<Double> = carritoRepository.obtenerSubtotal().map { it ?: 0.0 }
    
    // Costo de envío (gratis si > $50.000)
    val costoEnvio: Flow<Double> = subtotal.map { subtotal ->
        if (subtotal > 50000) 0.0 else 3990.0
    }
    
    // Total final
    val total: Flow<Double> = subtotal.map { subtotalValue ->
        val envio = if (subtotalValue > 50000) 0.0 else 3990.0
        subtotalValue + envio
    }

    // Estado del pago
    private val _paymentState = MutableLiveData<PaymentState>()
    val paymentState: LiveData<PaymentState> = _paymentState

    sealed class PaymentState {
        object Idle : PaymentState()
        object Loading : PaymentState()
        data class Success(val orderId: Int) : PaymentState()
        data class Error(val message: String) : PaymentState()
    }
    
    /**
     * Procesar el pago y crear la orden
     */
    fun procesarPago(numeroTarjeta: String, usuarioId: Int) {
        viewModelScope.launch {
            _paymentState.value = PaymentState.Loading
            try {
                // Simulación de validación: Si termina en par es aprobada
                val ultimoDigito = numeroTarjeta.lastOrNull()?.digitToIntOrNull() ?: -1
                val esAprobada = ultimoDigito % 2 == 0

                if (esAprobada) {
                    // Obtener datos actuales para la orden
                    val currentItems = items.first()
                    val currentSubtotal = subtotal.first()
                    val currentEnvio = costoEnvio.first()

                    if (currentItems.isEmpty()) {
                        _paymentState.value = PaymentState.Error("El carrito está vacío")
                        return@launch
                    }

                    // Crear orden en BD
                    val orderId = ordenRepository.crearOrden(
                        usuarioId = usuarioId,
                        itemsCarrito = currentItems,
                        subtotal = currentSubtotal,
                        costoEnvio = currentEnvio
                    )

                    // Vaciar carrito tras compra exitosa
                    carritoRepository.vaciarCarrito()

                    _paymentState.value = PaymentState.Success(orderId)
                } else {
                    _paymentState.value = PaymentState.Error("Pago rechazado por la entidad bancaria")
                }
            } catch (e: Exception) {
                _paymentState.value = PaymentState.Error("Error al procesar el pago: ${e.message}")
            }
        }
    }

    fun resetPaymentState() {
        _paymentState.value = PaymentState.Idle
    }
    
    /**
     * Agregar producto al carrito
     */
    fun agregarProducto(producto: Producto) {
        viewModelScope.launch {
            carritoRepository.agregarProducto(producto)
            android.util.Log.d("CarritoViewModel", "Producto agregado: ${producto.nombre}")
        }
    }
    
    /**
     * Actualizar cantidad de un item
     */
    fun actualizarCantidad(productoId: Int, cantidad: Int) {
        viewModelScope.launch {
            carritoRepository.actualizarCantidad(productoId, cantidad)
        }
    }
    
    /**
     * Incrementar cantidad de un item
     */
    fun incrementarCantidad(productoId: Int) {
        viewModelScope.launch {
            carritoRepository.incrementarCantidad(productoId)
        }
    }
    
    /**
     * Decrementar cantidad de un item
     */
    fun decrementarCantidad(productoId: Int) {
        viewModelScope.launch {
            carritoRepository.decrementarCantidad(productoId)
        }
    }
    
    /**
     * Eliminar item del carrito
     */
    fun eliminarItem(productoId: Int) {
        viewModelScope.launch {
            carritoRepository.eliminarItem(productoId)
        }
    }
    
    /**
     * Vaciar carrito
     */
    fun vaciarCarrito() {
        viewModelScope.launch {
            carritoRepository.vaciarCarrito()
        }
    }
}
