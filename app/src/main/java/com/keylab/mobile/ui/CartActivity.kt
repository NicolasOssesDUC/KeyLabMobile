package com.keylab.mobile.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.keylab.mobile.data.local.AppDatabase
import com.keylab.mobile.data.repository.CarritoRepository
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
    
    private val viewModel: CarritoViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = CarritoRepository(database.carritoDao())
        CarritoViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            Toast.makeText(this, "Proceder al pago - TODO", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatPrice(price: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        return format.format(price).replace(",", ".")
    }
}
