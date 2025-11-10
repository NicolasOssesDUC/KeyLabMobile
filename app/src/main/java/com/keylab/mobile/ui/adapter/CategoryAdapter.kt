package com.keylab.mobile.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.keylab.mobile.R
import com.keylab.mobile.databinding.ItemCategoryBinding

data class Category(
    val id: Int,
    val name: String,
    var isSelected: Boolean = false
)

class CategoryAdapter(
    private val onItemClick: (Category) -> Unit
) : ListAdapter<Category, CategoryAdapter.ViewHolder>(CategoryDiffCallback()) {

    private var selectedPosition = 0 // Primera categoría seleccionada por defecto

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition)
    }

    fun setSelectedPosition(position: Int) {
        val previousPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(previousPosition)
        notifyItemChanged(selectedPosition)
    }

    class ViewHolder(
        private val binding: ItemCategoryBinding,
        private val onItemClick: (Category) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category, isSelected: Boolean) {
            binding.tvCategoryName.text = category.name
            
            // Cambiar estilo según si está seleccionado
            val context = binding.root.context
            if (isSelected) {
                binding.categoryCard.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.dark_button_primary)
                )
                binding.tvCategoryName.setTextColor(
                    ContextCompat.getColor(context, R.color.dark_background)
                )
                binding.categoryCard.strokeWidth = 0
            } else {
                binding.categoryCard.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.dark_surface)
                )
                binding.tvCategoryName.setTextColor(
                    ContextCompat.getColor(context, R.color.dark_text_primary)
                )
                binding.categoryCard.strokeWidth = 2
            }
            
            binding.root.setOnClickListener {
                onItemClick(category)
            }
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }
}
