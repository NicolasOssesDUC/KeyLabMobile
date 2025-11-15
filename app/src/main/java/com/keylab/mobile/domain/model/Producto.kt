package com.keylab.mobile.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos que representa un producto del catálogo KeyLab.
 * 
 * Esta clase cumple dos funciones:
 * 1. Entidad de dominio (modelo de negocio)
 * 2. Entidad de Room (tabla SQLite local)
 * 
 * @Entity marca esta clase como una tabla de Room Database
 * tableName define el nombre de la tabla en SQLite
 */
@Entity(tableName = "productos")
data class Producto(

    @PrimaryKey
    val id: Int,
    val nombre: String,
    val precio: Double,
    val categoria: String,
    val subcategoria: String? = null,
    val descripcion: String? = null,
    val stock: Int,

    @SerializedName("imagen_url")
    @ColumnInfo(name = "imagen_url")  //cambia el nombre de la columna en la tabla
    val imagenUrl: String? = null,


    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    val createdAt: String? = null,

    @SerializedName("updated_at")
    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null
)
