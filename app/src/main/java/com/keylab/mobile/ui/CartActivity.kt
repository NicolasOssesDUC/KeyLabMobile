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
import com.keylab.mobile.data.repository.OrdenRepository
import com.keylab.mobile.databinding.ActivityCartBinding
import com.keylab.mobile.ui.adapter.CartAdapter
import com.keylab.mobile.ui.viewmodel.CarritoViewModel
import com.keylab.mobile.ui.viewmodel.CarritoViewModelFactory
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: CartAdapter
    private lateinit var preferencesManager: PreferencesManager
    
    private val viewModel: CarritoViewModel by viewModels {
        val db = AppDatabase.getDatabase(applicationContext)
        val carritoRepository = CarritoRepository(db.carritoDao())
        val ordenRepository = OrdenRepository(db.ordenDao())
        CarritoViewModelFactory(carritoRepository, ordenRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        viewModel.paymentState.observe(this) { state ->
            when (state) {
                is CarritoViewModel.PaymentState.Loading -> {
                    binding.btnCheckout.isEnabled = false
                    binding.btnCheckout.text = "Procesando..."
                }
                is CarritoViewModel.PaymentState.Success -> {
                    binding.btnCheckout.isEnabled = true
                    binding.btnCheckout.text = "Pagar"
                    
                    // Navegar a la boleta
                    val intent = Intent(this@CartActivity, OrderReceiptActivity::class.java)
                    intent.putExtra(OrderReceiptActivity.EXTRA_ORDER_ID, state.orderId)
                    // No limpiamos el back stack para que el usuario pueda volver atrás si lo desea,
                    // pero como el carrito está vacío, volver atrás mostrará el carrito vacío.
                    // Idealmente, 'OrderReceiptActivity' debería ser una nueva tarea o manejar la navegación.
                    // Para simplificar, cerramos CartActivity.
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                    viewModel.resetPaymentState()
                }
                is CarritoViewModel.PaymentState.Error -> {
                    binding.btnCheckout.isEnabled = true
                    binding.btnCheckout.text = "Pagar"
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                    viewModel.resetPaymentState()
                }
                else -> {
                     binding.btnCheckout.isEnabled = true
                     binding.btnCheckout.text = "Pagar"
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnCheckout.setOnClickListener {
             // Verificar que el usuario esté logueado
            val usuarioId = preferencesManager.obtenerUserId()
            if (usuarioId == -1) {
                Toast.makeText(
                    this,
                    "Debes iniciar sesión para realizar la compra",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val bottomSheet = PaymentBottomSheetFragment { cardNumber ->
                viewModel.procesarPago(cardNumber, usuarioId)
            }
            bottomSheet.show(supportFragmentManager, PaymentBottomSheetFragment.TAG)
        }
    }

    private fun formatPrice(price: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        return format.format(price).replace(",", ".")
    }
}
