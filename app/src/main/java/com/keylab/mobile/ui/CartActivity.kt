package com.keylab.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.keylab.mobile.R
import com.keylab.mobile.data.local.AppDatabase
import com.keylab.mobile.data.local.PreferencesManager
import com.keylab.mobile.data.repository.CarritoRepository
import com.keylab.mobile.databinding.ActivityCartBinding
import com.keylab.mobile.domain.model.Orden
import com.keylab.mobile.domain.model.OrdenItem
import com.keylab.mobile.ui.adapter.CartAdapter
import com.keylab.mobile.ui.viewmodel.CarritoViewModel
import com.keylab.mobile.ui.viewmodel.CarritoViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: CartAdapter
    private lateinit var database: AppDatabase
    private lateinit var preferencesManager: PreferencesManager
    
    private val viewModel: CarritoViewModel by viewModels {
        val db = AppDatabase.getDatabase(applicationContext)
        val repository = CarritoRepository(db.carritoDao())
        CarritoViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)
        preferencesManager = PreferencesManager(this)

        setupToolbar()
        setupRecyclerView()
        observeViewModel()
        setupListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = CartAdapter(
            onQuantityChanged = { item, quantity ->
                viewModel.actualizarCantidad(item.productoId, quantity)
            },
            onRemoveClick = { item ->
                viewModel.eliminarItem(item.productoId)
                Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@CartActivity)
            adapter = this@CartActivity.adapter
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.items.collect { items ->
                adapter.submitList(items)
                
                if (items.isEmpty()) {
                    binding.emptyStateLayout.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                    binding.summaryCard.visibility = View.GONE
                } else {
                    binding.emptyStateLayout.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.summaryCard.visibility = View.VISIBLE
                }
            }
        }
        
        lifecycleScope.launch {
            viewModel.subtotal.collect { subtotal ->
                binding.tvSubtotal.text = formatPrice(subtotal)
            }
        }
        
        lifecycleScope.launch {
            viewModel.costoEnvio.collect { envio ->
                if (envio > 0) {
                    binding.tvShipping.text = formatPrice(envio)
                } else {
                    binding.tvShipping.text = "GRATIS"
                }
            }
        }
        
        lifecycleScope.launch {
            viewModel.total.collect { total ->
                binding.tvTotal.text = formatPrice(total)
            }
        }
    }

    private fun setupListeners() {
        binding.btnCheckout.setOnClickListener {
            procesarCompra()
        }
    }

    private fun procesarCompra() {
        lifecycleScope.launch {
            try {
                // Verificar que el usuario esté logueado
                val usuarioId = preferencesManager.obtenerUserId()
                if (usuarioId == null) {
                    Toast.makeText(
                        this@CartActivity,
                        "Debes iniciar sesión para realizar la compra",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }
                
                // Obtener detalles del usuario (email, nombre si disponible)
                val usuarioEmail = preferencesManager.obtenerUserEmail() ?: "usuario@keylab.com"
                
                // Obtener items del carrito
                val items = viewModel.items.first()
                if (items.isEmpty()) {
                    Toast.makeText(
                        this@CartActivity,
                        getString(R.string.order_empty_cart),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                // Obtener totales
                val subtotal = viewModel.subtotal.first()
                val costoEnvio = viewModel.costoEnvio.first()
                val total = viewModel.total.first()

                // Generar número de orden único
                val numeroOrden = generarNumeroOrden()

                // Crear orden
                val orden = Orden(
                    usuarioId = usuarioId,
                    usuarioEmail = usuarioEmail,
                    usuarioNombre = "Usuario", // Placeholder
                    numeroOrden = numeroOrden,
                    subtotal = subtotal,
                    costoEnvio = costoEnvio,
                    total = total
                )

                // Insertar orden en la base de datos
                val ordenId = database.ordenDao().insertarOrden(orden)

                // Crear items de la orden
                val ordenItems = items.map { carritoItem ->
                    OrdenItem(
                        ordenId = ordenId.toInt(),
                        productoNombre = carritoItem.nombre,
                        cantidad = carritoItem.cantidad,
                        precioUnitario = carritoItem.precio,
                        subtotal = carritoItem.precio * carritoItem.cantidad
                    )
                }

                // Insertar items de la orden
                database.ordenDao().insertarOrdenItems(ordenItems)

                // Limpiar carrito
                items.forEach { carritoItem ->
                    viewModel.eliminarItem(carritoItem.productoId)
                }

                // Navegar a la boleta
                val intent = Intent(this@CartActivity, OrderReceiptActivity::class.java)
                intent.putExtra(OrderReceiptActivity.EXTRA_ORDER_ID, ordenId.toInt())
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@CartActivity,
                    getString(R.string.order_processing_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun generarNumeroOrden(): String {
        val fecha = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val random = (1..999).random().toString().padStart(3, '0')
        return "#ORD-$fecha-$random"
    }

    private fun formatPrice(price: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        return format.format(price).replace(",", ".")
    }
}
