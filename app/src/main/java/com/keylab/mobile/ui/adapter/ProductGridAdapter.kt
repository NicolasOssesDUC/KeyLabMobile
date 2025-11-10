package com.keylab.mobile.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.keylab.mobile.R
import com.keylab.mobile.databinding.ItemProductGridBinding
import com.keylab.mobile.domain.model.Producto

class ProductGridAdapter(
    private val onItemClick: (Producto) -> Unit,
    private val onFavoriteClick: (Producto) -> Unit
) : ListAdapter<Producto, ProductGridAdapter.ViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onItemClick, onFavoriteClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemProductGridBinding,
        private val onItemClick: (Producto) -> Unit,
        private val onFavoriteClick: (Producto) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(producto: Producto) {
            // Nombre
            binding.tvProductName.text = producto.nombre
            
            // Precio formateado CLP
            val precioFormateado = "$${String.format("%,.0f", producto.precio).replace(",", ".")}"
            binding.tvProductPrice.text = precioFormateado
            
            // Rating (mock - puedes agregar este campo a Producto si lo necesitas)
            binding.tvRating.text = "4.5"
            
            // Cargar imagen con Glide
            Glide.with(binding.root.context)
                .load(producto.imagenUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .centerCrop()
                .into(binding.ivProductImage)
            
            // Click listeners
            binding.root.setOnClickListener {
                onItemClick(producto)
            }
            
            binding.btnFavorite.setOnClickListener {
                onFavoriteClick(producto)
            }
        }
    }

    private class ProductDiffCallback : DiffUtil.ItemCallback<Producto>() {
        override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem == newItem
        }
    }
}
