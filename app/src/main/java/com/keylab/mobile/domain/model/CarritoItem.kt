package com.keylab.mobile.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Modelo para items en el carrito de compras
 * Persistido localmente en Room
 */
@Entity(tableName = "carrito_items")  //@define nombre de la tabla
data class CarritoItem(
    @PrimaryKey
    val productoId: Int,
    val nombre: String,
    val precio: Double,
    val categoria: String,
    val imagenUrl: String?,
    var cantidad: Int = 1,
    val fechaAgregado: Long = System.currentTimeMillis()
)
