package com.keylab.mobile.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.keylab.mobile.R
import com.keylab.mobile.data.local.AppDatabase
import com.keylab.mobile.data.repository.CarritoRepository
import com.keylab.mobile.data.repository.ProductoRepository
import com.keylab.mobile.data.remote.RetrofitClient
import com.keylab.mobile.databinding.ActivityProductDetailBinding
import com.keylab.mobile.domain.model.Producto
import com.keylab.mobile.ui.viewmodel.CarritoViewModel
import com.keylab.mobile.ui.viewmodel.CarritoViewModelFactory
import com.keylab.mobile.ui.viewmodel.ProductoViewModel
import com.keylab.mobile.ui.viewmodel.ProductoViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PRODUCTO_ID = "extra_producto_id"
    }

    private lateinit var binding: ActivityProductDetailBinding
    
    private val productoViewModel: ProductoViewModel by viewModels {
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

    private var currentProducto: Producto? = null
    private var cantidad = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupClickListeners()
        
        val productoId = intent.getIntExtra(EXTRA_PRODUCTO_ID, -1)
        if (productoId != -1) {
            loadProducto(productoId)
        } else {
            Toast.makeText(this, "Error: Producto no encontrado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Detalle del Producto"
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupClickListeners() {
        // Botón disminuir cantidad
        binding.btnDecrease.setOnClickListener {
            if (cantidad > 1) {
                cantidad--
                updateCantidad()
            }
        }

        // Botón aumentar cantidad
        binding.btnIncrease.setOnClickListener {
            currentProducto?.let { producto ->
                if (cantidad < producto.stock) {
                    cantidad++
                    updateCantidad()
                } else {
                    Toast.makeText(this, "Stock máximo alcanzado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Botón agregar al carrito
        binding.btnAddToCart.setOnClickListener {
            addToCart()
        }
    }

    private fun loadProducto(productoId: Int) {
        binding.progressBar.visibility = View.VISIBLE
        binding.contentLayout.visibility = View.GONE

        lifecycleScope.launch {
            try {
                // Obtener producto desde Room (offline-first)
                val productos = productoViewModel.productos.first()
                val producto = productos.find { it.id == productoId }

                if (producto != null) {
                    currentProducto = producto
                    displayProducto(producto)
                } else {
                    Toast.makeText(
                        this@ProductDetailActivity,
                        "Producto no encontrado",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ProductDetailActivity,
                    "Error al cargar producto: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun displayProducto(producto: Producto) {
        binding.contentLayout.visibility = View.VISIBLE

        // Nombre
        binding.tvProductName.text = producto.nombre

        // Precio
        binding.tvProductPrice.text = formatPrecio(producto.precio)

        // Categoría y subcategoría
        val categoriaText = if (producto.subcategoria != null) {
            "${producto.categoria} • ${producto.subcategoria}"
        } else {
            producto.categoria
        }
        binding.tvProductCategory.text = categoriaText

        // Descripción
        if (!producto.descripcion.isNullOrBlank()) {
            binding.tvProductDescription.text = producto.descripcion
            binding.tvProductDescription.visibility = View.VISIBLE
        } else {
            binding.tvProductDescription.text = "Sin descripción disponible"
            binding.tvProductDescription.visibility = View.VISIBLE
        }

        // Stock
        binding.tvStock.text = "Stock disponible: ${producto.stock} unidades"
        
        // Deshabilitar botones si no hay stock
        val hayStock = producto.stock > 0
        binding.btnDecrease.isEnabled = hayStock
        binding.btnIncrease.isEnabled = hayStock
        binding.btnAddToCart.isEnabled = hayStock
        
        if (!hayStock) {
            binding.tvStock.setTextColor(getColor(R.color.stock_out))
            binding.btnAddToCart.text = "Sin Stock"
        }

        // Imagen con Glide
        Glide.with(this)
            .load(producto.imagenUrl)
            .placeholder(R.drawable.ic_placeholder_image)
            .error(R.drawable.ic_placeholder_image)
            .centerCrop()
            .into(binding.ivProductImage)

        // Inicializar cantidad
        updateCantidad()
    }

    private fun updateCantidad() {
        binding.tvCantidad.text = cantidad.toString()
        
        // Calcular precio total
        currentProducto?.let { producto ->
            val total = producto.precio * cantidad
            binding.tvTotalPrice.text = formatPrecio(total)
        }
    }

    private fun addToCart() {
        currentProducto?.let { producto ->
            lifecycleScope.launch {
                try {
                    // Agregar producto al carrito (cantidad veces)
                    repeat(cantidad) {
                        carritoViewModel.agregarProducto(producto)
                    }
                    
                    Toast.makeText(
                        this@ProductDetailActivity,
                        "✓ ${cantidad}x ${producto.nombre} agregado al carrito",
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    // Resetear cantidad
                    cantidad = 1
                    updateCantidad()
                    
                } catch (e: Exception) {
                    Toast.makeText(
                        this@ProductDetailActivity,
                        "Error al agregar al carrito: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun formatPrecio(precio: Double): String {
        val formatted = String.format("%,.0f", precio)
        return "$${formatted.replace(",", ".")}"
    }
}
