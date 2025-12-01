package com.keylab.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.keylab.mobile.data.local.AppDatabase
import com.keylab.mobile.data.local.PreferencesManager
import com.keylab.mobile.data.repository.DireccionRepository
import com.keylab.mobile.databinding.ActivityAddressListBinding
import com.keylab.mobile.ui.adapter.AddressAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddressListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressListBinding
    private lateinit var adapter: AddressAdapter
    private lateinit var repository: DireccionRepository
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)
        repository = DireccionRepository(db.direccionDao())
        preferencesManager = PreferencesManager(this)

        setupToolbar()
        setupRecyclerView()
        setupFab()
        loadAddresses()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = AddressAdapter { direccion ->
            showDeleteConfirmation(direccion)
        }

        binding.recyclerViewAddresses.apply {
            layoutManager = LinearLayoutManager(this@AddressListActivity)
            adapter = this@AddressListActivity.adapter
        }
    }

    private fun setupFab() {
        binding.fabAddAddress.setOnClickListener {
            startActivity(Intent(this, AddAddressActivity::class.java))
        }
    }

    private fun loadAddresses() {
        val userId = preferencesManager.obtenerUserId()
        if (userId == -1) return

        lifecycleScope.launch {
            repository.obtenerDirecciones(userId).collectLatest { direcciones ->
                if (direcciones.isEmpty()) {
                    binding.emptyStateLayout.visibility = View.VISIBLE
                    binding.recyclerViewAddresses.visibility = View.GONE
                } else {
                    binding.emptyStateLayout.visibility = View.GONE
                    binding.recyclerViewAddresses.visibility = View.VISIBLE
                    adapter.submitList(direcciones)
                }
            }
        }
    }

    private fun showDeleteConfirmation(direccion: com.keylab.mobile.domain.model.Direccion) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar dirección")
            .setMessage("¿Estás seguro de que deseas eliminar '${direccion.alias}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteAddress(direccion.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteAddress(id: Int) {
        lifecycleScope.launch {
            repository.eliminarDireccion(id)
            Toast.makeText(this@AddressListActivity, "Dirección eliminada", Toast.LENGTH_SHORT).show()
        }
    }
}
