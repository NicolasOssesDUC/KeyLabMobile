package com.keylab.mobile.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.keylab.mobile.R
import com.keylab.mobile.databinding.ItemCartBinding
import com.keylab.mobile.domain.model.CarritoItem

class CartAdapter(
    private val onQuantityChanged: (CarritoItem, Int) -> Unit,
    private val onRemoveClick: (CarritoItem) -> Unit
) : ListAdapter<CarritoItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding, onQuantityChanged, onRemoveClick)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CartViewHolder(
        private val binding: ItemCartBinding,
        private val onQuantityChanged: (CarritoItem, Int) -> Unit,
        private val onRemoveClick: (CarritoItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CarritoItem) {
            binding.apply {
                // Nombre del producto
                tvProductoNombre.text = item.nombre

                // Precio unitario
                tvProductoPrecio.text = "$${String.format("%,d", item.precio.toInt()).replace(",", ".")}"

                // Cantidad
                tvQuantity.text = item.cantidad.toString()

                // Precio total (precio * cantidad)
                val precioTotal = item.precio * item.cantidad
                tvPrecioTotal.text = "$${String.format("%,d", precioTotal.toInt()).replace(",", ".")}"

                // Cargar imagen con Glide
                Glide.with(ivProductoImagen.context)
                    .load(item.imagenUrl)
                    .placeholder(R.drawable.ic_placeholder_image)
                    .error(R.drawable.ic_placeholder_image)
                    .centerCrop()
                    .into(ivProductoImagen)

                // Botón aumentar cantidad
                btnIncrease.setOnClickListener {
                    val newQuantity = item.cantidad + 1
                    onQuantityChanged(item, newQuantity)
                }

                // Botón disminuir cantidad
                btnDecrease.setOnClickListener {
                    if (item.cantidad > 1) {
                        val newQuantity = item.cantidad - 1
                        onQuantityChanged(item, newQuantity)
                    }
                }

                // Botón eliminar
                btnRemove.setOnClickListener {
                    onRemoveClick(item)
                }
            }
        }
    }

    private class CartDiffCallback : DiffUtil.ItemCallback<CarritoItem>() {
        override fun areItemsTheSame(oldItem: CarritoItem, newItem: CarritoItem): Boolean {
            return oldItem.productoId == newItem.productoId
        }

        override fun areContentsTheSame(oldItem: CarritoItem, newItem: CarritoItem): Boolean {
            return oldItem == newItem
        }
    }
}
