package com.keylab.mobile.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.keylab.mobile.data.local.AppDatabase
import com.keylab.mobile.data.local.PreferencesManager
import com.keylab.mobile.data.repository.DireccionRepository
import com.keylab.mobile.databinding.ActivityAddAddressBinding
import com.keylab.mobile.domain.model.Direccion
import kotlinx.coroutines.launch

class AddAddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAddressBinding
    private lateinit var repository: DireccionRepository
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)
        repository = DireccionRepository(db.direccionDao())
        preferencesManager = PreferencesManager(this)

        setupToolbar()
        setupListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            saveAddress()
        }
    }

    private fun saveAddress() {
        val alias = binding.etAlias.text.toString().trim()
        val calle = binding.etStreet.text.toString().trim()
        val numero = binding.etNumber.text.toString().trim()
        val depto = binding.etDepto.text.toString().trim()
        val comuna = binding.etComuna.text.toString().trim()
        val telefono = binding.etPhone.text.toString().trim()

        if (alias.isEmpty() || calle.isEmpty() || numero.isEmpty() || comuna.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Por favor completa los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = preferencesManager.obtenerUserId()
        if (userId == -1) {
            finish()
            return
        }

        val nuevaDireccion = Direccion(
            usuarioId = userId,
            alias = alias,
            calle = calle,
            numero = numero,
            depto = if (depto.isEmpty()) null else depto,
            comuna = comuna,
            telefono = telefono
        )

        lifecycleScope.launch {
            try {
                repository.agregarDireccion(nuevaDireccion)
                Toast.makeText(this@AddAddressActivity, "Dirección guardada", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@AddAddressActivity, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
