package com.keylab.mobile.data.repository

import com.keylab.mobile.data.local.OrdenDao
import com.keylab.mobile.domain.model.CarritoItem
import com.keylab.mobile.domain.model.Orden
import com.keylab.mobile.domain.model.OrdenItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repositorio para gestionar las órdenes de compra
 */
class OrdenRepository(private val ordenDao: OrdenDao) {

    /**
     * Crea una nueva orden a partir de los items del carrito
     */
    suspend fun crearOrden(usuarioId: Int, itemsCarrito: List<CarritoItem>, subtotal: Double, costoEnvio: Double): Int {
        val total = subtotal + costoEnvio
        val numeroOrden = "ORD-${UUID.randomUUID().toString().substring(0, 8).uppercase()}"
        
        val nuevaOrden = Orden(
            usuarioId = usuarioId,
            numeroOrden = numeroOrden,
            subtotal = subtotal,
            costoEnvio = costoEnvio,
            total = total
        )
        
        // Insertar orden y obtener ID generado
        val ordenId = ordenDao.insertarOrden(nuevaOrden).toInt()
        
        // Convertir CarritoItems a OrdenItems
        val ordenItems = itemsCarrito.map { item ->
            OrdenItem(
                ordenId = ordenId,
                productoNombre = item.nombre,
                cantidad = item.cantidad,
                precioUnitario = item.precio,
                subtotal = item.precio * item.cantidad
            )
        }
        
        // Insertar items de la orden
        ordenDao.insertarOrdenItems(ordenItems)
        
        return ordenId
    }
    
    fun obtenerOrdenesPorUsuario(usuarioId: Int): Flow<List<Orden>> {
        return ordenDao.obtenerOrdenesPorUsuario(usuarioId)
    }
    
    suspend fun obtenerOrdenPorId(ordenId: Int): Orden? {
        return ordenDao.obtenerOrdenPorId(ordenId)
    }
    
    suspend fun obtenerItemsPorOrden(ordenId: Int): List<OrdenItem> {
        return ordenDao.obtenerItemsPorOrden(ordenId)
    }
}
