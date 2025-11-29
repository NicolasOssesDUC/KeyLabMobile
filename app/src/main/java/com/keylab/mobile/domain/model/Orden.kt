package com.keylab.mobile.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

/**
 * Entidad Orden para registrar compras
 */
@Entity(tableName = "ordenes")
data class Orden(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    @ColumnInfo(name = "usuario_id")
    val usuarioId: String,

    @ColumnInfo(name = "usuario_email")
    val usuarioEmail: String?,

    @ColumnInfo(name = "usuario_nombre")
    val usuarioNombre: String?,
    
    @ColumnInfo(name = "numero_orden")
    val numeroOrden: String,
    
    val subtotal: Double,
    
    @ColumnInfo(name = "costo_envio")
    val costoEnvio: Double,
    
    val total: Double,
    
    @ColumnInfo(name = "fecha_orden")
    val fechaOrden: Long = System.currentTimeMillis(),
    
    val estado: String = "Completado"
)
