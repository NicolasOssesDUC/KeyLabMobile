package com.keylab.mobile.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.keylab.mobile.data.local.AppDatabase
import com.keylab.mobile.data.repository.OrdenRepository
import com.keylab.mobile.databinding.ActivityOrderReceiptBinding
import com.keylab.mobile.ui.adapter.OrderProductsAdapter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderReceiptActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderReceiptBinding
    private lateinit var repository: OrdenRepository
    private lateinit var database: AppDatabase
    private lateinit var adapter: OrderProductsAdapter

    companion object {
        const val EXTRA_ORDER_ID = "order_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Repositorio (Idealmente usar DI/ViewModel)
        database = AppDatabase.getDatabase(this)
        repository = OrdenRepository(database.ordenDao())

        setupRecyclerView()
        setupListeners()
        loadOrderData()
    }

    private fun setupRecyclerView() {
        adapter = OrderProductsAdapter()
        binding.recyclerViewProducts.apply {
            layoutManager = LinearLayoutManager(this@OrderReceiptActivity)
            adapter = this@OrderReceiptActivity.adapter
        }
    }

    private fun setupListeners() {
        binding.btnBackToHome.setOnClickListener {
            navigateToHome()
        }

        binding.btnViewOrders.setOnClickListener {
            // TODO: Implementar navegación directa al historial de órdenes cuando exista
            navigateToHome()
        }
    }
    
    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    private fun loadOrderData() {
        val ordenId = intent.getIntExtra(EXTRA_ORDER_ID, -1)
        if (ordenId == -1) {
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                val orden = repository.obtenerOrdenPorId(ordenId)
                val items = repository.obtenerItemsPorOrden(ordenId)
                
                // Para el usuario seguimos usando el DAO directo por ahora o Repository si existiera
                // Asumimos que existe un usuarioId válido
                 val usuarioFlow = database.usuarioDao().obtenerPorId(orden?.usuarioId ?: -1)

                orden?.let {
                    binding.tvOrderNumber.text = it.numeroOrden
                    binding.tvOrderDate.text = formatDate(it.fechaOrden)
                    binding.tvSubtotal.text = formatPrice(it.subtotal)
                    binding.tvShipping.text = if (it.costoEnvio > 0) {
                        formatPrice(it.costoEnvio)
                    } else {
                        "GRATIS"
                    }
                    binding.tvTotal.text = formatPrice(it.total)
                }
                
                // Recolectar datos de usuario
                usuarioFlow.firstOrNull()?.let { user ->
                     binding.tvCustomerName.text = user.nombre
                     binding.tvCustomerEmail.text = user.email
                }

                adapter.submitList(items)
            } catch (e: Exception) {
                e.printStackTrace()
                // En caso de error, podríamos mostrar un mensaje o cerrar
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale("es", "CL"))
        return sdf.format(Date(timestamp))
    }

    private fun formatPrice(price: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        return format.format(price).replace(",", ".")
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        navigateToHome()
    }
}
