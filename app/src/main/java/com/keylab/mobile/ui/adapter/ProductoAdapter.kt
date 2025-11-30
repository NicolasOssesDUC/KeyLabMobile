package com.keylab.mobile.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.keylab.mobile.R
import com.keylab.mobile.databinding.ItemProductoBinding
import com.keylab.mobile.domain.model.Producto

/**
 * Adapter para RecyclerView de productos
 * 
 * Usa ViewBinding para acceder a vistas
 * Usa Glide para cargar imágenes
 * Implementa DiffUtil para actualizaciones eficientes
 */
class ProductoAdapter(
    private val onItemClick: (Producto) -> Unit,
    private val onAddToCartClick: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    private var productos = emptyList<Producto>()
    
    // ViewHolder con ViewBinding
    inner class ProductoViewHolder(
        private val binding: ItemProductoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(producto: Producto) {
            // Nombre del producto
            binding.tvProductoNombre.text = producto.nombre
            
            // Precio formateado (CLP con puntos de miles)
            binding.tvProductoPrecio.text = formatPrecio(producto.precio)
            
            // Categoría (mostrar solo si existe)
            if (!producto.categoria.isNullOrEmpty()) {
                binding.chipCategoria.text = producto.categoria
                binding.chipCategoria.visibility = View.VISIBLE
            } else {
                binding.chipCategoria.visibility = View.GONE
            }
            
            // Badge de stock (mostrar solo si stock = 0)
            binding.chipSinStock.visibility = if (producto.stock == 0) {
                View.VISIBLE
            } else {
                View.GONE
            }
            
            // Cargar imagen con Glide
            Glide.with(binding.root.context)
                .load(producto.imagenUrl)
                .placeholder(R.mipmap.ic_launcher) // Placeholder mientras carga
                .error(R.mipmap.ic_launcher)       // Imagen si falla
                .centerCrop()
                .into(binding.ivProductoImagen)
            
            // Click en card completo (navega a detalle)
            binding.root.setOnClickListener {
                onItemClick(producto)
            }
            
            // Botón Agregar al Carrito
            binding.btnAgregarCarrito.setOnClickListener {
                onAddToCartClick(producto)
            }
            
            // Deshabilitar botón si no hay stock
            if (producto.stock == 0) {
                binding.btnAgregarCarrito.isEnabled = false
                binding.btnAgregarCarrito.text = "Sin Stock"
            } else {
                binding.btnAgregarCarrito.isEnabled = true
                binding.btnAgregarCarrito.text = binding.root.context.getString(R.string.agregar_al_carrito)
            }
        }
        
        // Formatea precio a CLP con puntos de miles
        private fun formatPrecio(precio: Double): String {
            val precioEntero = precio.toInt()
            return "$${String.format("%,d", precioEntero).replace(',', '.')}"
        }
    }
    
    // Crear ViewHolder (inflar XML)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val binding = ItemProductoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductoViewHolder(binding)
    }
    
    // Llenar ViewHolder con datos
    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(productos[position])
    }
    
    // Cantidad de items
    override fun getItemCount() = productos.size
    
    /**
     * Actualiza lista de productos (usa DiffUtil para eficiencia)
     */
    fun submitList(newList: List<Producto>) {
        val diffCallback = ProductoDiffCallback(productos, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        
        productos = newList
        diffResult.dispatchUpdatesTo(this)
    }
    
    /**
     * DiffUtil.Callback para comparar listas y actualizar solo cambios
     * Evita redibujar toda la lista cuando solo cambia un item
     */
    private class ProductoDiffCallback(
        private val oldList: List<Producto>,
        private val newList: List<Producto>
    ) : DiffUtil.Callback() {
        
        override fun getOldListSize() = oldList.size
        
        override fun getNewListSize() = newList.size
        
        // Compara si son el mismo item (por ID)
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }
        
        // Compara si el contenido cambió
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
