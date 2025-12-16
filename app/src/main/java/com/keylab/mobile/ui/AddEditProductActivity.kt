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
    private var selectedImageFile: java.io.File? = null
    private var currentProduct: Producto? = null // Para mantener datos originales (URL imagen)

    private val pickImageLauncher = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            binding.ivPreview.setImageURI(it)
            binding.ivPreview.imageTintList = null
            selectedImageFile = uriToFile(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupCategoriaDropdown()
        setupImageSelection()
        
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

    private fun setupImageSelection() {
        binding.btnSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
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

    private fun uriToFile(uri: android.net.Uri): java.io.File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = java.io.File(cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val outputStream = java.io.FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun cargarProducto(id: Int) {
        lifecycleScope.launch {
            val producto = viewModel.obtenerProductoPorId(id)
            if (producto != null) {
                currentProduct = producto // Guardar referencia
                with(binding) {
                    etNombre.setText(producto.nombre)
                    etPrecio.setText(producto.precio.toInt().toString())
                    etStock.setText(producto.stock.toString())
                    actvCategoria.setText(producto.categoria, false)
                    etSubcategoria.setText(producto.subcategoria)
                    etDescripcion.setText(producto.descripcion)
                    // etImageUrl.setText(producto.imagenUrl) // Removed
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
        // val imagenUrl = binding.etImageUrl.text.toString().trim() // Ya no usamos esto

        // Validaciones básicas
        if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty() || categoria.isEmpty()) {
            Toast.makeText(this, "Por favor completa los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val precio = precioStr.toDoubleOrNull() ?: 0.0
        val stock = stockStr.toIntOrNull() ?: 0

        // Si no hay archivo nuevo, usamos la URL que ya tenía el producto (si estamos editando)
        val finalImageUrl = if (selectedImageFile == null) currentProduct?.imagenUrl else null

        val producto = Producto(
            id = productoId ?: 0, // 0 para nuevo (backend ignora/autogenera)
            nombre = nombre,
            precio = precio,
            categoria = categoria,
            subcategoria = subcategoria.ifEmpty { null },
            descripcion = descripcion.ifEmpty { null },
            imagenUrl = finalImageUrl, // null si hay archivo (se llenará después) o URL existente
            stock = stock,
            createdAt = null,
            updatedAt = null
        )

        binding.loadingOverlay.visibility = View.VISIBLE
        
        if (productoId != null && productoId != -1) {
            viewModel.actualizarProducto(producto, selectedImageFile)
        } else {
            viewModel.crearProducto(producto, selectedImageFile)
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
