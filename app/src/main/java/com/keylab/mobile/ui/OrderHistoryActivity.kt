package com.keylab.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.keylab.mobile.data.local.AppDatabase
import com.keylab.mobile.data.local.PreferencesManager
import com.keylab.mobile.data.repository.OrdenRepository
import com.keylab.mobile.databinding.ActivityOrderHistoryBinding
import com.keylab.mobile.ui.adapter.OrderHistoryAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var adapter: OrderHistoryAdapter
    private lateinit var ordenRepository: OrdenRepository
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)
        ordenRepository = OrdenRepository(db.ordenDao())
        preferencesManager = PreferencesManager(this)

        setupToolbar()
        setupRecyclerView()
        loadOrders()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = OrderHistoryAdapter { orden ->
            // Navegar al detalle de la orden
            val intent = Intent(this, OrderReceiptActivity::class.java)
            intent.putExtra(OrderReceiptActivity.EXTRA_ORDER_ID, orden.id)
            startActivity(intent)
        }

        binding.recyclerViewOrders.apply {
            layoutManager = LinearLayoutManager(this@OrderHistoryActivity)
            adapter = this@OrderHistoryActivity.adapter
        }
    }

    private fun loadOrders() {
        val userId = preferencesManager.obtenerUserId()
        if (userId == -1) {
            finish() // No debería pasar si se accede desde Perfil
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            ordenRepository.obtenerOrdenesPorUsuario(userId).collectLatest { ordenes ->
                binding.progressBar.visibility = View.GONE
                
                if (ordenes.isEmpty()) {
                    binding.emptyStateLayout.visibility = View.VISIBLE
                    binding.recyclerViewOrders.visibility = View.GONE
                } else {
                    binding.emptyStateLayout.visibility = View.GONE
                    binding.recyclerViewOrders.visibility = View.VISIBLE
                    adapter.submitList(ordenes)
                }
            }
        }
    }
}
