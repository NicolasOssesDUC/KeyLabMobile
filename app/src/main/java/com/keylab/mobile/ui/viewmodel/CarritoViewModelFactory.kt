package com.keylab.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.keylab.mobile.data.repository.CarritoRepository
import com.keylab.mobile.data.repository.OrdenRepository

class CarritoViewModelFactory(
    private val carritoRepository: CarritoRepository,
    private val ordenRepository: OrdenRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CarritoViewModel::class.java)) {
            return CarritoViewModel(carritoRepository, ordenRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
