package com.keylab.mobile.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "direcciones")
data class Direccion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    @ColumnInfo(name = "usuario_id")
    val usuarioId: Int,
    
    val alias: String, // Ej: Casa, Oficina
    val calle: String,
    val numero: String,
    val depto: String? = null, // Opcional
    val comuna: String,
    val region: String = "Región Metropolitana",
    val telefono: String
)
