package com.keylab.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.keylab.mobile.R
import com.keylab.mobile.data.local.AppDatabase
import com.keylab.mobile.data.remote.RetrofitClient
import com.keylab.mobile.data.repository.CarritoRepository
import com.keylab.mobile.data.repository.ProductoRepository
import com.keylab.mobile.databinding.ActivityMainBinding
import com.keylab.mobile.ui.adapter.Category
import com.keylab.mobile.ui.adapter.CategoryAdapter
import com.keylab.mobile.ui.adapter.ProductoAdapter
import com.keylab.mobile.ui.viewmodel.CarritoViewModel
import com.keylab.mobile.ui.viewmodel.CarritoViewModelFactory
import com.keylab.mobile.ui.viewmodel.ProductoViewModel
import com.keylab.mobile.ui.viewmodel.ProductoViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    
    private val viewModel: ProductoViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ProductoRepository(
            dao = database.productoDao(),
            api = RetrofitClient.apiService
        )
        ProductoViewModelFactory(repository)
    }
    
    private val carritoViewModel: CarritoViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = CarritoRepository(database.carritoDao())
        CarritoViewModelFactory(repository)
    }
    
    private lateinit var productoAdapter: ProductoAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private var selectedCategory: String = "Todos" // Por defecto mostrar todos
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupCategoriesRecyclerView()
        setupProductsRecyclerView()
        setupObservers()
        setupClickListeners()
        
        // Sincronizar productos al iniciar
        viewModel.sincronizarProductos()
    }
    
    private fun setupCategoriesRecyclerView() {
        val categories = listOf(
            Category(0, "Todos", true),
            Category(1, "Teclados"),
            Category(2, "Keycaps"),
            Category(3, "Switches"),
            Category(4, "Cases")
        )
        
        categoryAdapter = CategoryAdapter { category ->
            // Actualizar categoría seleccionada
            selectedCategory = category.name
            val position = categories.indexOfFirst { it.name == category.name }
            categoryAdapter.setSelectedPosition(position)
            
            // Filtrar productos
            filterProducts()
        }
        
        binding.categoryRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
        
        categoryAdapter.submitList(categories)
    }
    
    private fun setupProductsRecyclerView() {
        productoAdapter = ProductoAdapter(
            onItemClick = { producto ->
                // Navegar a detalle de producto
                val intent = Intent(this, ProductDetailActivity::class.java)
                intent.putExtra(ProductDetailActivity.EXTRA_PRODUCTO_ID, producto.id)
                startActivity(intent)
            },
            onAddToCartClick = { producto ->
                // Agregar producto al carrito
                carritoViewModel.agregarProducto(producto)
                Toast.makeText(this, "✓ ${producto.nombre} agregado al carrito", Toast.LENGTH_SHORT).show()
            }
        )
        
        binding.productRecyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = productoAdapter
            setHasFixedSize(false)
        }
    }
    
    private fun setupObservers() {
        // Observar productos desde el ViewModel
        lifecycleScope.launch {
            viewModel.productos.collect { productos ->
                // Filtrar según categoría seleccionada
                val filteredProducts = if (selectedCategory == "Todos") {
                    productos.take(6) // Mostrar solo 6 cuando es "Todos"
                } else {
                    productos.filter { it.categoria == selectedCategory }
                }
                
                productoAdapter.submitList(filteredProducts)
                
                // Mostrar/ocultar empty state
                if (filteredProducts.isEmpty()) {
                    binding.emptyStateLayout.visibility = View.VISIBLE
                    binding.productRecyclerView.visibility = View.GONE
                } else {
                    binding.emptyStateLayout.visibility = View.GONE
                    binding.productRecyclerView.visibility = View.VISIBLE
                }
            }
        }
        
        // Observar estado de carga
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        // Observar errores
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, "Error: $it", Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
        
        // Observar mensajes de éxito
        viewModel.successMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearSuccessMessage()
            }
        }
    }
    
    private fun filterProducts() {
        // Refrescar la lista actual con el filtro
        lifecycleScope.launch {
            viewModel.productos.collect { productos ->
                val filteredProducts = if (selectedCategory == "Todos") {
                    productos.take(6)
                } else {
                    productos.filter { it.categoria == selectedCategory }
                }
                productoAdapter.submitList(filteredProducts)
                
                // Scroll al inicio de productos
                binding.productRecyclerView.smoothScrollToPosition(0)
                return@collect // Solo ejecutar una vez
            }
        }
    }
    
    private fun setupClickListeners() {
        // Ver todos los productos
        binding.tvViewAllProducts.setOnClickListener {
            // Cambiar a categoría "Todos"
            selectedCategory = "Todos"
            categoryAdapter.setSelectedPosition(0)
            filterProducts()
        }
        
        // Ver todas las categorías
        binding.tvViewAllCategories.setOnClickListener {
            Toast.makeText(this, "Ver todas las categorías", Toast.LENGTH_SHORT).show()
            // TODO: Navegar a pantalla de todas las categorías
        }
        
        // Bottom Navigation
        binding.bottomNav.selectedItemId = android.R.id.home // Shop por defecto
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                android.R.id.home -> {
                    // Ya estamos en Shop/Home
                    true
                }
                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    false // No cambiar selección, volvemos aquí
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    false // No cambiar selección, volvemos aquí
                }
                else -> false
            }
        }
    }
}
