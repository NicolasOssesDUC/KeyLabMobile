package com.keylab.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.keylab.mobile.R
import com.keylab.mobile.data.local.AppDatabase
import com.keylab.mobile.data.local.PreferencesManager
import com.keylab.mobile.data.remote.RetrofitClient
import com.keylab.mobile.data.repository.ProductoRepository
import com.keylab.mobile.databinding.ActivityAdminDashboardBinding
import com.keylab.mobile.domain.model.Producto
import com.keylab.mobile.ui.adapter.AdminProductoAdapter
import com.keylab.mobile.ui.viewmodel.ProductoViewModel
import com.keylab.mobile.ui.viewmodel.ProductoViewModelFactory
import kotlinx.coroutines.launch

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private lateinit var adapter: AdminProductoAdapter
    private lateinit var preferencesManager: PreferencesManager
    
    private val viewModel: ProductoViewModel by viewModels {
        ProductoViewModelFactory(
            ProductoRepository(
                AppDatabase.getDatabase(this).productoDao(),
                RetrofitClient.apiService
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesManager = PreferencesManager(this)

        setupToolbar()
        setupRecyclerView()
        setupFab()
        setupSwipeRefresh()
        observeViewModel()
        
        // Cargar productos iniciales
        viewModel.sincronizarProductos()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupRecyclerView() {
        adapter = AdminProductoAdapter(
            onEditClick = { producto ->
                val intent = Intent(this, AddEditProductActivity::class.java)
                intent.putExtra(AddEditProductActivity.EXTRA_PRODUCTO_ID, producto.id)
                startActivity(intent)
            },
            onDeleteClick = { producto ->
                showDeleteConfirmation(producto)
            }
        )

        binding.recyclerViewProductos.apply {
            layoutManager = GridLayoutManager(this@AdminDashboardActivity, 2)
            adapter = this@AdminDashboardActivity.adapter
        }
    }

    private fun setupFab() {
        binding.fabAddProduct.setOnClickListener {
            val intent = Intent(this, AddEditProductActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.sincronizarProductos()
        }
        
        // Color del spinner
        binding.swipeRefresh.setColorSchemeResources(R.color.keylab_primary)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.productos.collect { productos ->
                adapter.submitList(productos)

                if (productos.isEmpty()) {
                    binding.emptyStateLayout.visibility = View.VISIBLE
                    binding.recyclerViewProductos.visibility = View.GONE
                } else {
                    binding.emptyStateLayout.visibility = View.GONE
                    binding.recyclerViewProductos.visibility = View.VISIBLE
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (!isLoading) binding.swipeRefresh.isRefreshing = false
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
        
        viewModel.successMessage.observe(this) { msg ->
            msg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteConfirmation(producto: Producto) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Producto")
            .setMessage("¿Estás seguro de que deseas eliminar '${producto.nombre}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.eliminarProducto(producto.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bottom_nav, menu) // Reusamos layout temporalmente
        // Limpiar items que no necesitamos y dejar solo "Cerrar Sesión" si existiera
        menu?.clear()
        menu?.add(0, 1, 0, "Cerrar Sesión")?.setIcon(R.drawable.ic_arrow_back) // Placeholder icon
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            1 -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        preferencesManager.cerrarSesion()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
