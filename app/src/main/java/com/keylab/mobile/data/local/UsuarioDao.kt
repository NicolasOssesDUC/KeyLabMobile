package com.keylab.mobile.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.keylab.mobile.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(usuario: Usuario): Long

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    fun obtenerPorEmail(email: String): Flow<Usuario?>

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    fun obtenerPorId(id: Int): Flow<Usuario?>

    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password LIMIT 1")
    fun validarLogin(email: String, password: String): Flow<Usuario?>

    @Query("SELECT COUNT(*) > 0 FROM usuarios WHERE email = :email")
    fun emailExiste(email: String): Flow<Boolean>

    @Query("SELECt * FROM usuarios ORDER BY fecha_registro DESC")
    fun obtenerTodos(): Flow<List<Usuario>>

    @Query("SELECT COUNT(*) FROM usuarios")
    fun contarUsuarios(): Flow<Int>


}