package com.keylab.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.keylab.mobile.data.repository.ProductoRepository

/**
 * Factory para crear ProductoViewModel con Repository inyectado
 * 
 * Necesario porque ViewModel requiere parámetros en constructor
 * ViewModelProvider necesita saber cómo crear la instancia
 */
class ProductoViewModelFactory(
    private val repository: ProductoRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductoViewModel::class.java)) {
            return ProductoViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel class desconocida: ${modelClass.name}")
    }
}
