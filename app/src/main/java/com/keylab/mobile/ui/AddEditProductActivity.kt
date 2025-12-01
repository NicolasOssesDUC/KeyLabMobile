package com.keylab.mobile.ui

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.keylab.mobile.R
import com.keylab.mobile.data.local.AppDatabase
import com.keylab.mobile.data.remote.RetrofitClient
import com.keylab.mobile.data.repository.ProductoRepository
import com.keylab.mobile.databinding.ActivityAddEditProductBinding
import com.keylab.mobile.domain.model.Producto
import com.keylab.mobile.ui.viewmodel.ProductoViewModel
import com.keylab.mobile.ui.viewmodel.ProductoViewModelFactory
import kotlinx.coroutines.launch

class AddEditProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditProductBinding
    private val viewModel: ProductoViewModel by viewModels {
        ProductoViewModelFactory(
            ProductoRepository(
                AppDatabase.getDatabase(this).productoDao(),
                RetrofitClient.apiService
            )
        )
    }

    private var productoId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupCategoriaDropdown()
        setupImagePreview()
        
        // Verificar si es edición
        if (intent.hasExtra(EXTRA_PRODUCTO_ID)) {
            productoId = intent.getIntExtra(EXTRA_PRODUCTO_ID, -1)
            if (productoId != -1) {
                binding.toolbar.title = "Editar Producto"
                binding.btnGuardar.text = "Actualizar Producto"
                cargarProducto(productoId!!)
            }
        }

        binding.btnGuardar.setOnClickListener {
            guardarProducto()
        }

        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupCategoriaDropdown() {
        val categorias = resources.getStringArray(R.array.categorias_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categorias)
        binding.actvCategoria.setAdapter(adapter)
    }

    private fun setupImagePreview() {
        binding.etImageUrl.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                cargarImagenPreview(binding.etImageUrl.text.toString())
            }
        }
        
        // También actualizar al presionar enter o cambiar texto (opcional, puede ser mucho tráfico)
        // Por ahora solo onFocusChange es suficiente para UX básica
    }

    private fun cargarImagenPreview(url: String) {
        if (url.isNotEmpty()) {
            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_placeholder_image)
                .error(R.drawable.ic_placeholder_image)
                .into(binding.ivPreview)
            binding.ivPreview.imageTintList = null // Quitar tinte si carga imagen real
        }
    }

    private fun cargarProducto(id: Int) {
        lifecycleScope.launch {
            val producto = viewModel.obtenerProductoPorId(id)
            if (producto != null) {
                with(binding) {
                    etNombre.setText(producto.nombre)
                    etPrecio.setText(producto.precio.toInt().toString())
                    etStock.setText(producto.stock.toString())
                    actvCategoria.setText(producto.categoria, false)
                    etSubcategoria.setText(producto.subcategoria)
                    etDescripcion.setText(producto.descripcion)
                    etImageUrl.setText(producto.imagenUrl)
                    cargarImagenPreview(producto.imagenUrl ?: "")
                }
            } else {
                Toast.makeText(this@AddEditProductActivity, "Error cargando producto", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun guardarProducto() {
        val nombre = binding.etNombre.text.toString().trim()
        val precioStr = binding.etPrecio.text.toString().trim()
        val stockStr = binding.etStock.text.toString().trim()
        val categoria = binding.actvCategoria.text.toString().trim()
        val subcategoria = binding.etSubcategoria.text.toString().trim()
        val descripcion = binding.etDescripcion.text.toString().trim()
        val imagenUrl = binding.etImageUrl.text.toString().trim()

        // Validaciones básicas
        if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty() || categoria.isEmpty()) {
            Toast.makeText(this, "Por favor completa los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val precio = precioStr.toDoubleOrNull() ?: 0.0
        val stock = stockStr.toIntOrNull() ?: 0

        val producto = Producto(
            id = productoId ?: 0, // 0 para nuevo (backend ignora/autogenera)
            nombre = nombre,
            precio = precio,
            categoria = categoria,
            subcategoria = subcategoria,
            descripcion = descripcion,
            imagenUrl = imagenUrl.ifEmpty { null },
            stock = stock,
            createdAt = null,
            updatedAt = null
        )

        binding.loadingOverlay.visibility = View.VISIBLE
        
        if (productoId != null && productoId != -1) {
            viewModel.actualizarProducto(producto)
        } else {
            viewModel.crearProducto(producto)
        }
    }

    private fun observeViewModel() {
        viewModel.successMessage.observe(this) { msg ->
            msg?.let {
                binding.loadingOverlay.visibility = View.GONE
                Toast.makeText(this, "Guardado exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.error.observe(this) { err ->
            err?.let {
                binding.loadingOverlay.visibility = View.GONE
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        const val EXTRA_PRODUCTO_ID = "extra_producto_id"
    }
}
