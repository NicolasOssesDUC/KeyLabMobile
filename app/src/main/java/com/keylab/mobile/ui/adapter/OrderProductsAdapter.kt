package com.keylab.mobile.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.keylab.mobile.databinding.ItemOrderProductBinding
import com.keylab.mobile.domain.model.OrdenItem
import java.text.NumberFormat
import java.util.Locale

class OrderProductsAdapter : ListAdapter<OrdenItem, OrderProductsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemOrderProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OrdenItem) {
            binding.tvProductName.text = item.productoNombre
            binding.tvQuantity.text = "x${item.cantidad}"
            binding.tvPrice.text = formatPrice(item.subtotal)
        }

        private fun formatPrice(price: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
            return format.format(price).replace(",", ".")
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<OrdenItem>() {
        override fun areItemsTheSame(oldItem: OrdenItem, newItem: OrdenItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrdenItem, newItem: OrdenItem): Boolean {
            return oldItem == newItem
        }
    }
}
