package com.keylab.mobile.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.keylab.mobile.databinding.ItemAddressBinding
import com.keylab.mobile.domain.model.Direccion

class AddressAdapter(
    private val onDeleteClick: (Direccion) -> Unit
) : ListAdapter<Direccion, AddressAdapter.AddressViewHolder>(AddressDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AddressViewHolder(private val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(direccion: Direccion) {
            binding.tvAddressAlias.text = direccion.alias
            
            val line1 = if (direccion.depto.isNullOrBlank()) {
                "${direccion.calle} ${direccion.numero}"
            } else {
                "${direccion.calle} ${direccion.numero}, Depto ${direccion.depto}"
            }
            binding.tvAddressLine1.text = line1
            
            binding.tvAddressLine2.text = "${direccion.comuna}, ${direccion.region}"
            binding.tvPhone.text = direccion.telefono

            binding.btnDelete.setOnClickListener {
                onDeleteClick(direccion)
            }
        }
    }

    class AddressDiffCallback : DiffUtil.ItemCallback<Direccion>() {
        override fun areItemsTheSame(oldItem: Direccion, newItem: Direccion): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Direccion, newItem: Direccion): Boolean {
            return oldItem == newItem
        }
    }
}
