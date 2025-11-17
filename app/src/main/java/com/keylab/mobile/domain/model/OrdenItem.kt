package com.keylab.mobile.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

/**
 * Entidad para los items de cada orden
 */
@Entity(tableName = "orden_items")
data class OrdenItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    @ColumnInfo(name = "orden_id")
    val ordenId: Int,
    
    @ColumnInfo(name = "producto_nombre")
    val productoNombre: String,
    
    val cantidad: Int,
    
    @ColumnInfo(name = "precio_unitario")
    val precioUnitario: Double,
    
    val subtotal: Double
)
