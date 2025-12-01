package com.keylab.mobile.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.keylab.mobile.databinding.ItemOrderHistoryBinding
import com.keylab.mobile.domain.model.Orden
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderHistoryAdapter(
    private val onItemClick: (Orden) -> Unit
) : ListAdapter<Orden, OrderHistoryAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(private val binding: ItemOrderHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(orden: Orden) {
            binding.tvOrderNumber.text = orden.numeroOrden
            binding.tvOrderDate.text = formatDate(orden.fechaOrden)
            binding.tvOrderTotal.text = formatPrice(orden.total)
            binding.tvOrderStatus.text = orden.estado

            binding.root.setOnClickListener {
                onItemClick(orden)
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
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<Orden>() {
        override fun areItemsTheSame(oldItem: Orden, newItem: Orden): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Orden, newItem: Orden): Boolean {
            return oldItem == newItem
        }
    }
}
