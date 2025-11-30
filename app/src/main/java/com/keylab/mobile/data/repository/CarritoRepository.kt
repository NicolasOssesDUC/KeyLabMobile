package com.keylab.mobile.data.repository

import com.keylab.mobile.data.local.CarritoDao
import com.keylab.mobile.domain.model.CarritoItem
import com.keylab.mobile.domain.model.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repository para gestionar el carrito de compras
 * Persistencia local con Room
 */
class CarritoRepository(private val dao: CarritoDao) {
    
    // Obtener todos los items del carrito (Flow reactivo)
    fun obtenerItems(): Flow<List<CarritoItem>> = dao.obtenerItems()
    
    // Obtener cantidad de items
    fun contarItems(): Flow<Int> = dao.contarItems()
    
    // Obtener subtotal
    fun obtenerSubtotal(): Flow<Double?> = dao.obtenerSubtotal()
    
    /**
     * Agregar producto al carrito
     * Si ya existe, incrementa la cantidad
     */
    suspend fun agregarProducto(producto: Producto) {
        withContext(Dispatchers.IO) {
            val itemExistente = dao.obtenerPorProductoId(producto.id)
            
            if (itemExistente != null) {
                // Ya existe, incrementar cantidad
                dao.actualizarCantidad(producto.id, itemExistente.cantidad + 1)
            } else {
                // Nuevo item
                val nuevoItem = CarritoItem(
                    productoId = producto.id,
                    nombre = producto.nombre,
                    precio = producto.precio,
                    categoria = producto.categoria,
                    imagenUrl = producto.imagenUrl,
                    cantidad = 1
                )
                dao.insertar(nuevoItem)
            }
        }
    }
    
    /**
     * Actualizar cantidad de un item
     */
    suspend fun actualizarCantidad(productoId: Int, cantidad: Int) {
        withContext(Dispatchers.IO) {
            if (cantidad <= 0) {
                dao.eliminarPorId(productoId)
            } else {
                dao.actualizarCantidad(productoId, cantidad)
            }
        }
    }
    
    /**
     * Incrementar cantidad
     */
    suspend fun incrementarCantidad(productoId: Int) {
        withContext(Dispatchers.IO) {
            val item = dao.obtenerPorProductoId(productoId)
            item?.let {
                dao.actualizarCantidad(productoId, it.cantidad + 1)
            }
        }
    }
    
    /**
     * Decrementar cantidad
     */
    suspend fun decrementarCantidad(productoId: Int) {
        withContext(Dispatchers.IO) {
            val item = dao.obtenerPorProductoId(productoId)
            item?.let {
                if (it.cantidad > 1) {
                    dao.actualizarCantidad(productoId, it.cantidad - 1)
                } else {
                    dao.eliminarPorId(productoId)
                }
            }
        }
    }
    
    /**
     * Eliminar item del carrito
     */
    suspend fun eliminarItem(productoId: Int) {
        withContext(Dispatchers.IO) {
            dao.eliminarPorId(productoId)
        }
    }
    
    /**
     * Vaciar carrito completo
     */
    suspend fun vaciarCarrito() {
        withContext(Dispatchers.IO) {
            dao.vaciarCarrito()
        }
    }
}
