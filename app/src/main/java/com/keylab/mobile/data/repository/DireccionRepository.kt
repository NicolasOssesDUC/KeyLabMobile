package com.keylab.mobile.data.repository

import com.keylab.mobile.data.local.DireccionDao
import com.keylab.mobile.domain.model.Direccion
import kotlinx.coroutines.flow.Flow

class DireccionRepository(private val direccionDao: DireccionDao) {

    fun obtenerDirecciones(usuarioId: Int): Flow<List<Direccion>> {
        return direccionDao.obtenerDireccionesPorUsuario(usuarioId)
    }

    suspend fun agregarDireccion(direccion: Direccion) {
        direccionDao.insertar(direccion)
    }

    suspend fun eliminarDireccion(id: Int) {
        direccionDao.eliminarPorId(id)
    }
}
