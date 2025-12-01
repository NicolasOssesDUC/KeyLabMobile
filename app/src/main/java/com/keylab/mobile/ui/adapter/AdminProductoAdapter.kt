package com.keylab.mobile.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.keylab.mobile.R
import com.keylab.mobile.databinding.ItemProductoAdminBinding
import com.keylab.mobile.domain.model.Producto
import java.text.NumberFormat
import java.util.Locale

class AdminProductoAdapter(
    private val onEditClick: (Producto) -> Unit,
    private val onDeleteClick: (Producto) -> Unit
) : ListAdapter<Producto, AdminProductoAdapter.ProductoViewHolder>(ProductoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val binding = ItemProductoAdminBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductoViewHolder(private val binding: ItemProductoAdminBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(producto: Producto) {
            binding.apply {
                tvProductoNombre.text = producto.nombre
                tvStock.text = "Stock: ${producto.stock}"
                
                // Formatear precio CLP
                val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
                var precioStr = format.format(producto.precio)
                precioStr = precioStr.replace(",", ".") // Reemplazar coma por punto (estilo chileno)
                if (precioStr.endsWith(".00")) {
                    precioStr = precioStr.substring(0, precioStr.length - 3)
                }
                tvProductoPrecio.text = precioStr

                // Cargar imagen
                Glide.with(itemView.context)
                    .load(producto.imagenUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.ic_placeholder_image)
                    .error(R.drawable.ic_placeholder_image)
                    .into(ivProductoImagen)

                // Listeners
                btnEditar.setOnClickListener { onEditClick(producto) }
                btnEliminar.setOnClickListener { onDeleteClick(producto) }
                
                // Icono editar (usamos placeholder si no existe, pero lo definimos en XML)
                // btnEditar.setIconResource(R.drawable.ic_edit) 
            }
        }
    }

    class ProductoDiffCallback : DiffUtil.ItemCallback<Producto>() {
        override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem == newItem
        }
    }
}
