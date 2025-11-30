package com.keylab.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.keylab.mobile.data.local.AppDatabase
import com.keylab.mobile.data.remote.RetrofitClient
import com.keylab.mobile.data.repository.ProductoRepository
import com.keylab.mobile.databinding.ActivityCategoryBinding
import com.keylab.mobile.ui.adapter.ProductoAdapter
import com.keylab.mobile.ui.viewmodel.ProductoViewModel
import com.keylab.mobile.ui.viewmodel.ProductoViewModelFactory

class CategoryActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityCategoryBinding
    private lateinit var viewModel: ProductoViewModel
    private lateinit var adapter: ProductoAdapter
    private var categoria: String = ""
    
    companion object {
        const val EXTRA_CATEGORIA = "extra_categoria"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Obtener categoría del intent
        categoria = intent.getStringExtra(EXTRA_CATEGORIA) ?: "Teclados"
        
        setupToolbar()
        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupListeners()
        
        // Cargar productos de la categoría
        viewModel.filtrarPorCategoria(categoria)
    }
    
    private fun setupToolbar() {
        binding.toolbar.title = categoria
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(this)
        val repository = ProductoRepository(
            dao = database.productoDao(),
            api = RetrofitClient.apiService
        )
        val factory = ProductoViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ProductoViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        adapter = ProductoAdapter(
            onItemClick = { producto ->
                // Navegar a detalle de producto
                val intent = Intent(this, ProductDetailActivity::class.java)
                intent.putExtra(ProductDetailActivity.EXTRA_PRODUCTO_ID, producto.id)
                startActivity(intent)
            },
            onAddToCartClick = { producto ->
                // Agregar al carrito (aquí necesitarás el CarritoViewModel)
                Toast.makeText(this, "✓ ${producto.nombre} agregado al carrito", Toast.LENGTH_SHORT).show()
            }
        )
        
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@CategoryActivity, 2)
            adapter = this@CategoryActivity.adapter
            setHasFixedSize(true)
        }
    }
    
    private fun setupObservers() {
        // Observar productos (Flow convertido a LiveData)
        viewModel.productos.asLiveData().observe(this) { productos ->
            val productosFiltrados = productos.filter { it.categoria == categoria }
            adapter.submitList(productosFiltrados)
            
            // Mostrar empty state si no hay productos
            if (productosFiltrados.isEmpty()) {
                binding.emptyStateLayout.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.emptyStateLayout.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        }
        
        // Observar loading
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.swipeRefresh.isRefreshing = isLoading
        }
        
        // Observar errores
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun setupListeners() {
        // Pull to refresh
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.sincronizarProductos()
        }
    }
}
